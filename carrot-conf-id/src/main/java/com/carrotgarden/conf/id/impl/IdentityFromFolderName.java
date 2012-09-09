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

public class IdentityFromFolderName extends IdentityFromUnknown {

	protected IdentityFromFolderName(final Constant constValues) {
		super(constValues);
	}

	@Override
	protected String getValue() {

		try {

			final String progHome = System.getProperty("user.dir");

			if (progHome == null || progHome.length() == 0) {
				log.debug("missing 'user.dir' property");
				return null;
			}

			final File file = new File(progHome);

			final String suffix = constValues.idFolderDomainSuffix();

			final File parent = Util.findFolderWithSuffix(file, suffix);

			if (parent == null) {
				log.debug("parent folder not found; suffix : {}", suffix);
				return null;
			}

			return parent.getName();

		} catch (final Throwable e) {

			log.debug("'user.dir' parent folder lookup failure", e);

			return null;

		}

	}

	@Override
	public Source getSource() {
		return Source.PARENT_FOLDER_NAME;
	}

}
