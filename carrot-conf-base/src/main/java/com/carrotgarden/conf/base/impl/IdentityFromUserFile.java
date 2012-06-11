package com.carrotgarden.conf.base.impl;

import java.io.File;

import com.carrotgarden.conf.base.api.ConfigConst;
import com.carrotgarden.conf.base.api.IdentitySource;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class IdentityFromUserFile extends IdentityFromUnknown {

	@Override
	protected String getValue() {

		try {

			final String userHome = System.getProperty("user.home");

			if (userHome == null || userHome.length() == 0) {
				log.debug("missing user.home");
				return null;
			}

			final String fileName = ConfigConst.Id.USER_FILE_NAME;

			final File file = new File(userHome, fileName);

			if (!file.exists() || !file.isFile() || !file.canRead()) {
				log.debug("missing conf file");
				return null;
			}

			final Config conf = ConfigFactory.parseFile(file);

			return conf.getString(ConfigConst.Id.SYSTEM_PROPERTY);

		} catch (final Throwable e) {

			log.debug("bad conf file", e);

			return null;

		}

	}

	@Override
	public IdentitySource getSource() {
		return IdentitySource.USER_FILE;
	}

}
