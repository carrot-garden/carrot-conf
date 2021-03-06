/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.id.impl;

import com.carrotgarden.conf.id.api.Constant;

public class IdentityFromEnvironment extends IdentityFromUnknown {

	protected IdentityFromEnvironment(final Constant constValues) {
		super(constValues);
	}

	@Override
	protected String getValue() {

		final String value = System.getenv(constValues.idEnvironmentVariable());

		if (value == null || value.length() == 0) {
			log.debug("environment variable not found : {}",
					constValues.idEnvironmentVariable());
			return INVALID_ID;
		}

		return value;

	}

	@Override
	public Source getSource() {
		return Source.ENVIRONMENT_VARIABLE;
	}

}
