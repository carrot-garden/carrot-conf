/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.repo.impl;

import java.io.File;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.id.api.Constant;
import com.carrotgarden.conf.id.api.Identity;
import com.carrotgarden.conf.id.api.IdentityService;
import com.carrotgarden.conf.list.ConfigListBuilder;
import com.carrotgarden.conf.repo.api.ConfigService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@Component
public class ConfigServiceProvider implements ConfigService {

	private static final Logger log = LoggerFactory
			.getLogger(ConfigServiceProvider.class);

	private final static Config INVALID = ConfigFactory.empty("invalid config");

	private final static Identity UNKNOWN = new IdentityUnknown();

	private Constant constant;

	private Identity identity = UNKNOWN;

	private IdentityService identityService;

	private Config masterConfig = INVALID;

	private RepoService repo;

	private Config versionConfig = INVALID;

	private boolean isActive;

	@Activate
	protected void activate() {

		log.debug("activate");

		final Config conf = Util.reference().getConfig(
				constant().keyRepository());

		repo = new RepoServiceImpl(conf);

		repo.ensureRepoAll();

		isActive = true //
				&& updateIdentity() //
				&& updateVersion() //
				&& updateMaster();

	}

	@Deactivate
	protected void deactivate() {

		repo = null;

		log.debug("deactivate");

	}

	private Constant constant() {
		if (constant == null) {
			constant = Util.constant();
		}
		return constant;
	}

	@Override
	public Identity getIdentity() {
		return identity;
	}

	@Override
	public Config getMasterConfig() {
		return masterConfig;
	}

	@Override
	public File getMasterRoot() {
		return repo.getLocalMaster();
	}

	@Override
	public File getMasterInstance() {
		return getRootOrInstance(getMasterRoot());
	}

	@Override
	public Config getVersionConfig() {
		return versionConfig;
	}

	@Override
	public File getVersionRoot() {
		return repo.getLocalVersion();
	}

	@Override
	public File getVersionInstance() {
		return getRootOrInstance(getVersionRoot());
	}

	private File getRootOrInstance(final File root) {
		final File instanceFolder = instanceFolder(root);
		if (instanceFolder == null) {
			return root;
		} else {
			return instanceFolder;
		}
	}

	//

	@Override
	public boolean isIdentityValid() {
		return identity.isValid();
	}

	@Override
	public boolean isMasterValid() {
		return masterConfig != INVALID;
	}

	@Override
	public boolean isVersionValid() {
		return versionConfig != INVALID;
	}

	//

	@Override
	public boolean updateIdentity() {
		final Identity current = identityService.getCurrentIdentity();
		if (current.isValid()) {
			identity = current;
		}
		return identity.isValid();
	}

	@Override
	public boolean updateMaster() {

		if (!isIdentityValid()) {
			log.error("", new IllegalStateException("must have identity"));
			return false;
		}

		if (!isVersionValid()) {
			log.error("", new IllegalStateException("must have version"));
			return false;
		}

		/** must have version entry */
		final String versionTag;
		try {
			versionTag = getVersionConfig().getString(constant().keyVersion());
		} catch (final Exception e) {
			log.error("missing version entry", e);
			return false;
		}

		/** discover new master */
		if (!repo.updateRepoMaster(versionTag)) {
			log.error("", new Exception("update master failure"));
			return false;
		}

		//

		final File instanceFolder = instanceFolder(repo.getLocalMaster());

		if (instanceFolder == null) {
			log.error("", new IllegalArgumentException(
					"missing instance master folder"));
			return false;
		}

		log.debug("instance folder : {}", instanceFolder);

		final String configName = constant().repoFileApplication();

		final File configFile = new File(instanceFolder, configName);

		log.debug("instance file   : {}", configFile);

		masterConfig = parse(configFile, masterConfig);

		return isMasterValid();

	}

	/** find instance folder based on root, identity, name convention */
	private File instanceFolder(final File repoRoot) {

		/** instance root : /instance */
		final String instanceRoot = constant().repoFolderInstance();

		/** start with /instance/com/example/karaf/ */
		String searchPath = instancePath();

		/** iterate upward for cluster config discovery */
		while (true) {

			final File instanceFolder = new File(repoRoot, searchPath);

			if (instanceFolder.exists() && instanceFolder.isDirectory()) {
				/** found folder */
				return instanceFolder;
			}

			/** trim : /instance/com/example/karaf/ -> /instance/com/example/ */
			searchPath = Util.instancePathTrimLast(instanceRoot, searchPath);

			if (instanceRoot.equals(searchPath)) {
				/** nothing found */
				return null;
			}

		}

	}

	@Override
	public boolean updateVersion() {

		if (!isIdentityValid()) {
			log.error("", new IllegalStateException("must have identity"));
			return false;
		}

		if (!repo.updateRepoArchon()) {
			log.error("", new Exception("update archon failure"));
			return false;
		}

		if (!repo.updateRepoVersion()) {
			log.error("", new Exception("update version failure"));
			return false;
		}

		//

		final File instanceFolder = instanceFolder(repo.getLocalVersion());

		if (instanceFolder == null) {
			log.error("", new IllegalArgumentException(
					"missing instance version folder"));
			return false;
		}

		log.debug("instance folder : {}", instanceFolder);

		final String configName = constant().repoFileVersion();

		final File configFile = new File(instanceFolder, configName);

		log.debug("instance file   : {}", configFile);

		versionConfig = parse(configFile, versionConfig);

		return isVersionValid();

	}

	private String instancePath() {

		final String root = constant().repoFolderInstance();

		final String id = getIdentity().getId();

		final String pathBase = Util.instancePathFromInstanceId(root, id);

		return pathBase;

	}

	private Config parse(final File file, final Config defaultConfig) {
		try {

			/** parse configuration */
			final Config step1 = ConfigFactory.parseFile(file);

			/** perform substitutions */
			final Config step2 = step1.resolve();

			/** apply list builder */
			final Config step3 = ConfigListBuilder.process(step2);

			return step3;

		} catch (final Exception e) {
			log.error("invalid config format", e);
			return defaultConfig;
		}
	}

	@Reference
	protected void bind(final IdentityService s) {
		identityService = s;
	}

	protected void unbind(final IdentityService s) {
		identityService = null;
	}

	@Override
	public void injectMaster(File configFile) {

		if (configFile == null) {

			final String configName = constant().repoFileApplication();

			configFile = new File(getMasterInstance(), configName);

		}

		masterConfig = parse(configFile, INVALID);

	}

}
