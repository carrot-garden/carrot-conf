package com.carrotgarden.conf.base.impl;

import java.io.File;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.base.api.ConfigConst;
import com.carrotgarden.conf.base.api.ConfigService;
import com.carrotgarden.conf.base.api.Identity;
import com.carrotgarden.conf.base.api.IdentityService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@Component(immediate = true)
public class ConfigServiceProvider implements ConfigService {

	private static final Logger log = LoggerFactory
			.getLogger(ConfigServiceProvider.class);

	private final static Config INVALID = ConfigFactory.empty("invalid config");

	private Config config = INVALID;

	@Override
	public void updateConfig() {

		/** discover identity */

		final Identity identity = identityService.getCurrentIdentity();

		if (identity == null || !identity.isAvailable()) {
			log.error("invalid identity", new Exception());
			return;
		}

		final String id = identity.getId();

		final String pathBase = Util.getInstancePathFromInstanceId(id);

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

		final String versionPath = pathBase + "/"
				+ ConfigConst.Repo.FILE_VERSION;

		final File versionRoot = repo.getLocalVersion();

		final File versionFile = new File(versionRoot, versionPath);

		final Config versionConfig = ConfigFactory.parseFile(versionFile);

		final String versionTag = versionConfig
				.getString(ConfigConst.Key.VERSION);

		/** discover new master */

		if (!repo.updateRepoMaster(versionTag)) {
			log.error("update master failure", new Exception());
			return;
		}

		final File appRoot = repo.getLocalMaster();

		String appPath = pathBase + "/" + ConfigConst.Repo.FILE_APPLICAION;

		Config appConfig = INVALID;

		while (true) {

			/** iterate upward for cluster config */

			final File appFile = new File(appRoot, appPath);

			if (appFile.exists()) {
				appConfig = ConfigFactory.parseFile(appFile);
				log.debug("master config file : {}", appFile);
				break;
			}

			appPath = Util.getInstancePathTrimLast(appPath);

			if (ConfigConst.Repo.DIR_INSTANCE.equals(appPath)) {
				log.error("master config file missing", new Exception());
				break;
			}

		}

		config = appConfig;

	}

	@Override
	public boolean isConfigAvailable() {
		return config != INVALID;
	}

	@Override
	public Config getInstanceConfig() {
		return config;
	}

	@Override
	public File getConfigFolder() {
		return repo.getLocalMaster();
	}

	//

	private RepoService repo;

	@Activate
	protected void activate() {

		log.debug("### actvate");

		final Config boot = ConfigFactory.load(ConfigConst.Repo.BOOT_FILE);
		final Config conf = boot.getConfig(ConfigConst.Key.REPOSITORY);

		repo = new RepoServiceImpl(conf);

		repo.ensureRepoAll();

		updateConfig();

	}

	@Deactivate
	protected void deactivate() {

		repo = null;

		log.debug("### deactvate");

	}

	//

	private IdentityService identityService;

	@Reference
	protected void bind(final IdentityService s) {
		identityService = s;
	}

	protected void unbind(final IdentityService s) {
		identityService = null;
	}

}
