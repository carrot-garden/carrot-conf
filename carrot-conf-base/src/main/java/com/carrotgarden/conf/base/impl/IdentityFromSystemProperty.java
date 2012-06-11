package com.carrotgarden.conf.base.impl;

import com.carrotgarden.conf.base.api.ConfigConst;
import com.carrotgarden.conf.base.api.IdentitySource;

public class IdentityFromSystemProperty extends IdentityFromUnknown {

	@Override
	protected String getValue() {

		final String value = System
				.getProperty(ConfigConst.Id.SYSTEM_PROPERTY);

		if (value == null || value.length() == 0) {
			log.debug("missing sys prop");
		}

		return value;

	}

	@Override
	public IdentitySource getSource() {
		return IdentitySource.SYSTEM_PROPERTY;
	}

}
