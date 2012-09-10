package com.carrotgarden.conf.file;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestFileWatch {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test0() {

		final File folder = new File("./target");

		assertTrue(FileWatch.pathAdd(folder));

		assertTrue(FileWatch.pathCheck(folder).isEmpty());

		assertTrue(FileWatch.pathRemove(folder));

	}

	@Test
	public void test1() throws Exception {

		final File folder = new File("./target");

		final String path = "file-" + UUID.randomUUID().toString();

		final File file = new File(folder, path);

		assertTrue(FileWatch.pathAdd(folder));

		assertTrue(FileWatch.pathCheck(folder).isEmpty());

		assertTrue(file.createNewFile());

		List<File> list = null;

		for (int k = 0; k < 10; k++) {
			list = FileWatch.pathCheck(folder);
			if (!list.isEmpty()) {
				break;
			}
			Thread.sleep(200);
		}

		assertEquals(1, list.size());

		assertEquals(file.getName(), list.get(0).getName());

		assertTrue(FileWatch.pathRemove(folder));

	}

}
