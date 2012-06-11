package com.carrotgarden.conf.base.impl;

import com.carrotgarden.conf.base.api.ConfigConst;
import com.carrotgarden.conf.base.api.IdentitySource;

public class IdentityFromEnvironment extends IdentityFromUnknown {

	@Override
	protected String getValue() {

		final String value = System
				.getenv(ConfigConst.Id.ENVIRONMENT_VARIABLE);

		if (value == null || value.length() == 0) {
			log.debug("missing env var");
		}

		return value;

	}

	@Override
	public IdentitySource getSource() {
		return IdentitySource.ENVIRONMENT_VARIABLE;
	}

}
