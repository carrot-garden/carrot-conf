package com.carrotgarden.conf.base.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.base.api.Identity;
import com.carrotgarden.conf.base.api.IdentityService;
import com.carrotgarden.conf.base.api.IdentitySource;

@Component
public class IdentityServiceProvider implements IdentityService {

	final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Identity getCurrentIdentity() {

		for (final IdentitySource source : IdentitySource.values()) {

			final Identity identity = source.newIdentity();

			if (identity.isAvailable()) {
				return identity;
			}

		}

		return IdentitySource.UNKNONW.newIdentity();

	}

	@Activate
	protected void activate() {
		//
	}

	@Deactivate
	protected void deactivate() {
		//
	}

}
