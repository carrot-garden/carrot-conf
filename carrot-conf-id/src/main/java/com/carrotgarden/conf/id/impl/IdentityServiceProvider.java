/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.id.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.id.api.Identity;
import com.carrotgarden.conf.id.api.IdentityService;
import com.carrotgarden.conf.id.api.Identity.Source;

@Component
public class IdentityServiceProvider implements IdentityService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private Constant constValues;

	private Constant constValues() {
		if (constValues == null) {
			constValues = Util.constant();
		}
		return constValues;
	}

	@Override
	public Identity getCurrentIdentity() {

		final Constant constValues = constValues();

		for (final Source source : Source.values()) {

			final Identity identity;

			switch (source) {
			case ENVIRONMENT_VARIABLE:
				identity = new IdentityFromEnvironment(constValues);
				break;
			case SYSTEM_PROPERTY:
				identity = new IdentityFromSystemProperty(constValues);
				break;
			case PROGRAM_HOME:
				identity = new IdentityFromProgramHome(constValues);
				break;
			case USER_HOME:
				identity = new IdentityFromUserHome(constValues);
				break;
			case AMAZON_URL_EC2:
				identity = new IdentityFromAmazonEC2(constValues);
				break;
			default:
				identity = new IdentityFromUnknown(constValues);
				break;
			}

			if (identity.isAvailable()) {
				return identity;
			}

		}

		return new IdentityFromUnknown(constValues);

	}

	@Activate
	protected void activate() {
		log.debug("activate");
	}

	@Deactivate
	protected void deactivate() {
		log.debug("deactivate");
	}

}
