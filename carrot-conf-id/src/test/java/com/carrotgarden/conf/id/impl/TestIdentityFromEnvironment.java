/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.id.impl;

import static org.testng.AssertJUnit.*;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import util.JDK;

import com.carrotgarden.conf.id.api.Constant;
import com.carrotgarden.conf.id.api.Identity;

public class TestIdentityFromEnvironment {

	private final static Logger log = LoggerFactory
			.getLogger(TestIdentityFromEnvironment.class);

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	@Test
	public void testIdentity() {

		final Constant constValues = Util.constant();

		final String propName = constValues.idEnvironmentVariable();

		JDK.setEnv(propName, null);

		final Identity id0 = new IdentityFromEnvironment(constValues);
		assertFalse(id0.isValid());
		assertEquals(id0.getId(), "");

		//

		final String value = UUID.randomUUID().toString();

		JDK.setEnv(propName, value);

		final Identity id1 = new IdentityFromEnvironment(constValues);
		assertTrue(id1.isValid());
		assertEquals(id1.getId(), value);

		//

		JDK.setEnv(propName, null);

	}

}
