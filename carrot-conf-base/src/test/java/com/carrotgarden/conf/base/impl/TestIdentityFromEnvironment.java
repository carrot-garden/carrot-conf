/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.base.impl;

import static org.testng.AssertJUnit.*;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import util.JDK;

import com.carrotgarden.conf.base.api.ConfigConst;
import com.carrotgarden.conf.base.api.Identity;
import com.carrotgarden.conf.base.api.IdentitySource;

public class TestIdentityFromEnvironment {

	final static Logger log = LoggerFactory
			.getLogger(TestIdentityFromEnvironment.class);

	protected void setUp() throws Exception {

	}

	protected void tearDown() throws Exception {
	}

	@Test
	public void testIdentity() {

		JDK.setEnv(ConfigConst.Id.ENVIRONMENT_VARIABLE, null);

		final Identity id0 = IdentitySource.ENVIRONMENT_VARIABLE.newIdentity();
		assertFalse(id0.isAvailable());
		assertEquals(id0.getId(), "");

		//

		final String value = UUID.randomUUID().toString();

		JDK.setEnv(ConfigConst.Id.ENVIRONMENT_VARIABLE, value);

		final Identity id1 = IdentitySource.ENVIRONMENT_VARIABLE.newIdentity();
		assertTrue(id1.isAvailable());
		assertEquals(id1.getId(), value);

		//

		JDK.setEnv(ConfigConst.Id.ENVIRONMENT_VARIABLE, null);

	}

}
