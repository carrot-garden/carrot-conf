/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.base.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.base.api.Identity;
import com.carrotgarden.conf.base.api.IdentitySource;

public class IdentityFromUnknown implements Identity {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected String getValue() {
		return null;
	}

	@Override
	public boolean isAvailable() {
		final String id = getValue();
		return id != null && id.length() != 0;
	}

	@Override
	public String getId() {
		if (isAvailable()) {
			return getValue();
		} else {
			return "";
		}
	}

	@Override
	public IdentitySource getSource() {
		return IdentitySource.UNKNONW;
	}

}
