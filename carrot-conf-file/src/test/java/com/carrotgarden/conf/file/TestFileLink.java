/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.file;

import static org.testng.AssertJUnit.*;

import java.io.File;

import org.testng.annotations.Test;

public class TestFileLink {

	static final String LINK = "./target/@build";
	static final String FILE = "./build";

	@Test
	public void testMakeLink() throws Exception {

		final File link = new File(LINK);

		link.delete();
		assertFalse(link.exists());

		final File file = new File(FILE);
		assertTrue(file.exists());

		assertTrue(FileLink.makeLink(link, file));
		assertTrue(link.exists());

		link.delete();
		assertFalse(link.exists());

	}

	@Test
	public void ensureLink1() throws Exception {

		final File link = new File(LINK);

		final File file = new File(FILE);

		link.delete();
		assertFalse(link.exists());

		assertTrue(FileLink.ensureLink(link, file));
		assertTrue(link.exists());

		link.delete();
		assertFalse(link.exists());

	}

	@Test
	public void ensureLink2() throws Exception {

		final File link = new File(LINK);

		final File file = new File(FILE);

		link.delete();
		assertFalse(link.exists());

		assertTrue(FileLink.ensureLink(link, file));
		assertTrue(link.exists());

		assertTrue(FileLink.ensureLink(link, file));
		assertTrue(link.exists());

		link.delete();
		assertFalse(link.exists());

	}

	@Test
	public void ensureLink3() throws Exception {

		final File link = new File(LINK);

		final File file = new File(FILE);

		link.delete();
		assertFalse(link.exists());

		assertTrue(link.mkdir());
		assertTrue(link.exists());

		assertTrue(FileLink.ensureLink(link, file));
		assertTrue(link.exists());

		link.delete();
		assertFalse(link.exists());

	}

	@Test
	public void ensureLink4() throws Exception {

		final File link = new File(LINK);
		final File test = new File(LINK, "test-file");

		final File file = new File(FILE);

		link.delete();
		assertFalse(link.exists());

		assertTrue(link.mkdir());
		assertTrue(link.exists());

		assertTrue(test.createNewFile());
		assertTrue(test.exists());

		assertTrue(FileLink.ensureLink(link, file));
		assertTrue(link.exists());

		link.delete();
		assertFalse(link.exists());

	}

	@Test
	public void ensureLink5() throws Exception {

		final File link = new File(LINK);

		final File file = new File(FILE);
		final File fileTest = new File(FILE + "test");

		link.delete();
		assertFalse(link.exists());

		assertTrue(FileLink.makeLink(link, fileTest));
		assertFalse(link.exists());

		assertTrue(FileLink.ensureLink(link, file));
		assertTrue(link.exists());

		assertTrue(FileLink.ensureLink(link, file));
		assertTrue(link.exists());

		link.delete();
		assertFalse(link.exists());

	}

}
