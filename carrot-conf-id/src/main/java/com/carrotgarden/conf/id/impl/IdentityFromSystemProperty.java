/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.id.impl;

import com.carrotgarden.conf.id.api.Constant;

public class IdentityFromSystemProperty extends IdentityFromUnknown {

	protected IdentityFromSystemProperty(final Constant constValues) {
		super(constValues);
	}

	@Override
	protected String getValue() {

		final String value = System.getProperty(constValues.idSystemProperty());

		if (value == null || value.length() == 0) {
			log.debug("system property not found : {}",
					constValues.idSystemProperty());
		}

		return value;

	}

	@Override
	public Source getSource() {
		return Source.SYSTEM_PROPERTY;
	}

}
