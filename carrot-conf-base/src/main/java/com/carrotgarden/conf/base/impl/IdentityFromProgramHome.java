/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.base.impl;

import java.io.File;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class IdentityFromProgramHome extends IdentityFromUnknown {

	protected IdentityFromProgramHome(final ConstValues constValues) {
		super(constValues);
	}

	@Override
	protected String getValue() {

		try {

			final String progHome = System.getProperty("user.dir");

			if (progHome == null || progHome.length() == 0) {
				log.debug("missing user.dir");
				return null;
			}

			final String fileName = constValues.idProgramHomeFile();

			final File file = new File(progHome, fileName);

			if (!file.exists() || !file.isFile() || !file.canRead()) {
				log.debug("missing conf file");
				return null;
			}

			final Config conf = ConfigFactory.parseFile(file);

			return conf.getString(constValues.keyIdentity());

		} catch (final Throwable e) {

			log.debug("bad conf file", e);

			return null;

		}

	}

	@Override
	public Source getSource() {
		return Source.PROGRAM_HOME;
	}

}
