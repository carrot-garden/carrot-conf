package com.carrotgarden.conf.base.api;

public interface ConfigConst {

	/** configuration keys */
	interface Key {

		/** instancae id */
		String INSTANCE = "carrot.config.instance";

		/** instance version */
		String VERSION = "carrot.config.version";

		/** repository descriptor */
		String REPOSITORY = "carrot.config.repository";

	}

	/** identity sources properties */
	interface Id {

		/** name of system property that could provide instance id */
		String SYSTEM_PROPERTY = Key.INSTANCE;

		/** name of envrionment variable that could provide instance id */
		String ENVIRONMENT_VARIABLE = "CARROT_CONFIG_INSTANCE";

		/** name of ${user.home}/file that could provide instance id */
		String USER_FILE_NAME = "carrot-config-instance.conf";

		/** amazon ec2 url that could provide instance id */
		String AMAZON_EC2_URL = "http://169.254.169.254/latest/user-data"
		// disable compile time constant for test hack
				.toString();

	}

	/** repository layout properties */
	interface Repo {

		/** custom protocl name */
		String PROTOCOL = "config";

		/** initial configuration file */
		String BOOT_FILE = "carrot-configuration.conf";

		/** instance root folder */
		String DIR_INSTANCE = "/instance";

		/** instance version selector file in the version branch */
		String FILE_VERSION = "version.conf";

		/** instance configuration file in the master branch */
		String FILE_APPLICAION = "application.conf";

	}

}
