/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.base.impl;

import java.io.File;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.base.api.ConfigService;
import com.carrotgarden.conf.base.api.Identity;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@Component
public class ConfigServiceProvider implements ConfigService {

	private static final Logger log = LoggerFactory
			.getLogger(ConfigServiceProvider.class);

	private final static Config INVALID = ConfigFactory.empty("invalid config");

	private final static Identity UNKNOWN = new IdentityFromUnknown(null);

	private ConstValues constValues;

	private Identity identity = UNKNOWN;

	private IdentityService identityService;

	private Config masterConfig = INVALID;

	private RepoService repo;

	private Config versionConfig = INVALID;

	@Activate
	protected void activate() {

		log.debug("activate");

		final Config conf = reference()
				.getConfig(constValues().keyRepository());

		repo = new RepoServiceImpl(conf);

		repo.ensureRepoAll();

		updateIdentity();
		updateVersion();
		updateMaster();

	}

	@Deactivate
	protected void deactivate() {

		repo = null;

		log.debug("deactivate");

	}

	private ConstValues constValues() {
		if (constValues == null) {
			constValues = Util.constValues();
		}
		return constValues;
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
		return identity.isAvailable();
	}

	@Override
	public boolean isMasterValid() {
		return masterConfig != INVALID;
	}

	@Override
	public boolean isVersionValid() {
		return versionConfig != INVALID;
	}

	private Config reference() {
		final ClassLoader loader = ConfigServiceProvider.class.getClassLoader();
		return ConfigFactory.defaultReference(loader);
	}

	//

	@Override
	public void updateIdentity() {
		identity = identityService.getCurrentIdentity();
	}

	@Override
	public void updateMaster() {

		/** must have identity */
		if (!isIdentityValid()) {
			log.error("invalid identity", new Exception());
			return;
		}

		/** must have version */
		if (!isVersionValid()) {
			log.error("invalid version", new Exception());
			return;
		}

		/** must have version entry */
		final String versionTag;
		try {
			versionTag = getVersionConfig().getString(
					constValues().keyVersion());
		} catch (final Exception e) {
			log.error("missing version entry", e);
			return;
		}

		/** discover new master */
		if (!repo.updateRepoMaster(versionTag)) {
			log.error("update master failure", new Exception());
			return;
		}

		//

		final File instanceFolder = instanceFolder(repo.getLocalMaster());

		if (instanceFolder == null) {
			masterConfig = INVALID;
			log.error("missing instance folder", new Exception());
			return;
		}

		log.debug("instance folder : {}", instanceFolder);

		final String configName = constValues().repoFileApplication();

		final File configFile = new File(instanceFolder, configName);

		log.debug("instance file   : {}", configFile);

		masterConfig = parse(configFile);

	}

	/** find instance folder based on root, identity, name convention */
	private File instanceFolder(final File repoRoot) {

		/** instance root : /instance */
		final String instanceRoot = constValues().repoFolderInstance();

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
	public void updateVersion() {

		/** must have identity */
		if (!isIdentityValid()) {
			log.error("invalid identity", new Exception());
			return;
		}

		/** fetch from remote */
		if (!repo.updateRepoArchon()) {
			log.error("update archon failure", new Exception());
			return;
		}

		/** discover new version */
		if (!repo.updateRepoVersion()) {
			log.error("update version failure", new Exception());
			return;
		}

		//

		final File instanceFolder = instanceFolder(repo.getLocalVersion());

		if (instanceFolder == null) {
			versionConfig = INVALID;
			log.error("missing instance folder", new Exception());
			return;
		}

		log.debug("instance folder : {}", instanceFolder);

		final String configName = constValues().repoFileVersion();

		final File configFile = new File(instanceFolder, configName);

		log.debug("instance file   : {}", configFile);

		versionConfig = parse(configFile);

	}

	private String instancePath() {

		final String root = constValues().repoFolderInstance();

		final String id = getIdentity().getId();

		final String pathBase = Util.instancePathFromInstanceId(root, id);

		return pathBase;

	}

	private Config parse(final File file) {
		try {
			return ConfigFactory.parseFile(file);
		} catch (final Exception e) {
			log.error("", e);
			return INVALID;
		}
	}

	@Reference
	protected void bind(final IdentityService s) {
		identityService = s;
	}

	protected void unbind(final IdentityService s) {
		identityService = null;
	}

}
