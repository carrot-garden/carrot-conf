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

import com.carrotgarden.conf.id.api.Constant;
import com.carrotgarden.conf.id.api.Identity;
import com.carrotgarden.conf.id.api.Identity.Source;
import com.carrotgarden.conf.id.api.IdentityService;

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

	/** default */
	private final Identity INVALID = new IdentityFromUnknown(constValues());

	/** previous identity, if any */
	private Identity previousIdentity = INVALID;

	@Override
	public Identity getCurrentIdentity() {

		final Constant constValues = constValues();

		Identity currentIdentity = INVALID;

		/** iterate in Source declaration order */
		for (final Source source : Source.values()) {

			switch (source) {
			case ENVIRONMENT_VARIABLE:
				currentIdentity = new IdentityFromEnvironment(constValues);
				break;
			case SYSTEM_PROPERTY:
				currentIdentity = new IdentityFromSystemProperty(constValues);
				break;
			case PARENT_FOLDER_NAME:
				currentIdentity = new IdentityFromFolderName(constValues);
				break;
			case PROGRAM_HOME_FILE:
				currentIdentity = new IdentityFromProgramHome(constValues);
				break;
			case USER_HOME_FILE:
				currentIdentity = new IdentityFromUserHome(constValues);
				break;
			case AMAZON_URL_EC2:
				currentIdentity = new IdentityFromAmazonEC2(constValues);
				break;
			default:
				currentIdentity = new IdentityFromUnknown(constValues);
				break;
			}

			if (currentIdentity.isValid()) {
				break;
			}

		}

		if (!previousIdentity.equals(currentIdentity)) {

			log.info("identity change; \n\t old : {} \n\t new : {}",
					previousIdentity, currentIdentity);

			previousIdentity = currentIdentity;

		}

		System.setProperty(constValues.activeIdentity(),
				currentIdentity.getId());

		return currentIdentity;

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
