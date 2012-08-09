/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.base.impl;


public class IdentityFromSystemProperty extends IdentityFromUnknown {

	protected IdentityFromSystemProperty(final ConstValues constValues) {
		super(constValues);
	}

	@Override
	protected String getValue() {

		final String value = System.getProperty(constValues.idSystemProperty());

		if (value == null || value.length() == 0) {
			log.debug("missing sys prop");
		}

		return value;

	}

	@Override
	public Source getSource() {
		return Source.SYSTEM_PROPERTY;
	}

}
