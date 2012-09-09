/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.id.impl;

import java.io.File;

import com.carrotgarden.conf.id.api.Constant;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class IdentityFromUserHome extends IdentityFromUnknown {

	protected IdentityFromUserHome(final Constant constValues) {
		super(constValues);
	}

	@Override
	protected String getValue() {

		try {

			final String userHome = System.getProperty("user.home");

			if (userHome == null || userHome.length() == 0) {
				log.debug("missing 'user.home'");
				return null;
			}

			final String fileName = constValues.idUserHomeFile();

			final File file = new File(userHome, fileName);

			if (!file.exists() || !file.isFile() || !file.canRead()) {
				log.debug("missing 'user.home' config file : {}", fileName);
				return null;
			}

			final Config conf = ConfigFactory.parseFile(file);

			return conf.getString(constValues.keyIdentity());

		} catch (final Throwable e) {

			log.debug("'user.home' config file lookup failure", e);

			return null;

		}

	}

	@Override
	public Source getSource() {
		return Source.USER_HOME_FILE;
	}

}
