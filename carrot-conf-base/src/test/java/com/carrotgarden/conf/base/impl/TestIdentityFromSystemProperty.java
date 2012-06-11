package com.carrotgarden.conf.base.impl;

import static org.testng.AssertJUnit.*;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.carrotgarden.conf.base.api.ConfigConst;
import com.carrotgarden.conf.base.api.Identity;
import com.carrotgarden.conf.base.api.IdentitySource;

public class TestIdentityFromSystemProperty {

	private final static Logger log = LoggerFactory
			.getLogger(TestIdentityFromSystemProperty.class);

	protected void setUp() throws Exception {

	}

	protected void tearDown() throws Exception {
	}

	@Test
	public void testIdentity() {

		System.setProperty(ConfigConst.Id.SYSTEM_PROPERTY, "");

		final Identity id0 = IdentitySource.SYSTEM_PROPERTY.newIdentity();
		assertFalse(id0.isAvailable());
		assertEquals(id0.getId(), "");

		//

		final String value = UUID.randomUUID().toString();

		System.setProperty(ConfigConst.Id.SYSTEM_PROPERTY, value);

		final Identity id1 = IdentitySource.SYSTEM_PROPERTY.newIdentity();
		assertTrue(id1.isAvailable());
		assertEquals(id1.getId(), value);

		//

		System.setProperty(ConfigConst.Id.SYSTEM_PROPERTY, "");

	}

}
