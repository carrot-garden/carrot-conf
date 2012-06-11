package com.carrotgarden.conf.base.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.base.impl.IdentityFromAmazonEC2;
import com.carrotgarden.conf.base.impl.IdentityFromEnvironment;
import com.carrotgarden.conf.base.impl.IdentityFromSystemProperty;
import com.carrotgarden.conf.base.impl.IdentityFromUnknown;
import com.carrotgarden.conf.base.impl.IdentityFromUserFile;

/** known identity discovery sources */
public enum IdentitySource {

	// note: enum ordinal is selector priority

	ENVIRONMENT_VARIABLE(IdentityFromEnvironment.class), //

	SYSTEM_PROPERTY(IdentityFromSystemProperty.class), //

	USER_FILE(IdentityFromUserFile.class), //

	AMAZON_EC2(IdentityFromAmazonEC2.class), //

	//

	UNKNONW(IdentityFromUnknown.class), // default

	;

	private final static Logger log = LoggerFactory
			.getLogger(IdentitySource.class);

	private final Class<? extends Identity> klaz;

	private IdentitySource(final Class<? extends Identity> klaz) {
		this.klaz = klaz;
	}

	public Identity newIdentity() {
		try {
			return klaz.newInstance();
		} catch (final Throwable e) {
			log.error("identity problem", e);
			return new IdentityFromUnknown();
		}
	}

}
