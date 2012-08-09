/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.base.impl;

import static org.testng.AssertJUnit.*;

import java.io.File;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.carrotgarden.conf.base.api.Identity;

public class TestIdentityFromUserHome {

	private final static Logger log = LoggerFactory
			.getLogger(TestIdentityFromUserHome.class);

	@BeforeClass
	public static void testInit() {

		log.debug("###");

	}

	protected void setUp() throws Exception {

	}

	protected void tearDown() throws Exception {
	}

	@Test
	public void testIdentity() throws Exception {

		final ConstValues constValues = Util.constValues();

		final String userHome = System.getProperty("user.home");

		final String fileName = constValues.idUserHomeFile();

		final File file = new File(userHome, fileName);

		//

		file.delete();
		assertFalse(file.exists());

		//

		final Identity id0 = new IdentityFromUserHome(constValues);
		assertFalse(id0.isAvailable());
		assertEquals(id0.getId(), "");

		//

		final String value = UUID.randomUUID().toString();

		final String propName = constValues.keyIdentity();

		final String text = propName + " = " + "\"" + value + "\"";

		Util.saveFileAsString(file.getAbsolutePath(), text);
		assertTrue(file.exists());

		//

		final Identity id1 = new IdentityFromUserHome(constValues);
		assertTrue(id1.isAvailable());
		assertEquals(id1.getId(), value);

		//

		file.delete();
		assertFalse(file.exists());

	}

}
