package com.carrotgarden.conf.base.impl;

import static org.testng.AssertJUnit.*;

import java.io.File;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.carrotgarden.conf.base.api.ConfigConst;
import com.carrotgarden.conf.base.api.Identity;
import com.carrotgarden.conf.base.api.IdentitySource;
import com.carrotgarden.conf.base.impl.Util;

public class TestIdentityFromUserFile {

	private final static Logger log = LoggerFactory
			.getLogger(TestIdentityFromUserFile.class);

	protected void setUp() throws Exception {

	}

	protected void tearDown() throws Exception {
	}

	@Test
	public void testIdentity() throws Exception {

		final String userHome = System.getProperty("user.home");

		final String fileName = ConfigConst.Id.USER_FILE_NAME;

		final File file = new File(userHome, fileName);

		//

		file.delete();
		assertFalse(file.exists());

		//

		final Identity id0 = IdentitySource.USER_FILE.newIdentity();
		assertFalse(id0.isAvailable());
		assertEquals(id0.getId(), "");

		//

		final String value = UUID.randomUUID().toString();

		final String text = //
		ConfigConst.Id.SYSTEM_PROPERTY + " = " + "\"" + value + "\"";

		Util.saveFileAsString(file.getAbsolutePath(), text);
		assertTrue(file.exists());

		//

		final Identity id1 = IdentitySource.USER_FILE.newIdentity();
		assertTrue(id1.isAvailable());
		assertEquals(id1.getId(), value);

		//

		file.delete();
		assertFalse(file.exists());

	}

}
