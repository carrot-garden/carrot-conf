/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.id.impl;

import static org.testng.AssertJUnit.*;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.carrotgarden.conf.id.api.Identity;
import com.carrotgarden.conf.id.api.IdentityService;
import com.carrotgarden.conf.id.impl.IdentityServiceProvider;
import com.carrotgarden.conf.id.impl.Util;

public class TestConfigService {

	@BeforeTest
	protected void setUp() throws Exception {

		final String propName = Util.constant().idSystemProperty();

		System.setProperty(propName, "karaf.company.com");

	}

	@AfterTest
	protected void tearDown() throws Exception {
	}

	@Test
	public void testGetIdentity() {

		final IdentityService service = new IdentityServiceProvider();

		final Identity identity = service.getCurrentIdentity();

		assertTrue(identity.isValid());

		assertEquals(identity.getId(), "karaf.company.com");

	}

}
