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

import com.carrotgarden.conf.base.api.Identity;

public class TestIdentityFromSystemProperty {

	private final static Logger log = LoggerFactory
			.getLogger(TestIdentityFromSystemProperty.class);

	protected void setUp() throws Exception {

	}

	protected void tearDown() throws Exception {
	}

	@Test
	public void testIdentity() {

		final ConstValues constValues = Util.constValues();

		final String propName = constValues.idSystemProperty();

		System.setProperty(propName, "");

		final Identity id0 = new IdentityFromSystemProperty(constValues);
		assertFalse(id0.isAvailable());
		assertEquals(id0.getId(), "");

		//

		final String value = UUID.randomUUID().toString();

		System.setProperty(propName, value);

		final Identity id1 = new IdentityFromSystemProperty(constValues);
		assertTrue(id1.isAvailable());
		assertEquals(id1.getId(), value);

		//

		System.setProperty(propName, "");

	}

}
