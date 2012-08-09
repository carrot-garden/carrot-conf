/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.base.impl;


public class IdentityFromEnvironment extends IdentityFromUnknown {

	protected IdentityFromEnvironment(final ConstValues constValues) {
		super(constValues);
	}

	@Override
	protected String getValue() {

		final String value = System.getenv(constValues.idEnvironmentVariable());

		if (value == null || value.length() == 0) {
			log.debug("missing env var");
		}

		return value;

	}

	@Override
	public Source getSource() {
		return Source.ENVIRONMENT_VARIABLE;
	}

}
