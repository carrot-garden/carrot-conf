/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.repo.impl;

import static org.testng.AssertJUnit.*;
import static org.testng.FileAssert.*;

import java.io.File;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestRepoServiceImpl {

	private static final Logger log = LoggerFactory
			.getLogger(TestRepoServiceImpl.class);

	private static final String id = "karaf.company.com";

	private static final String local = "./target/"
			+ TestRepoServiceImpl.class.getSimpleName() + "-"
			+ UUID.randomUUID().toString();

	private static RepoService repo;

	@BeforeClass
	public static void testInit() {

		System.setProperty("carrot.config.identity", id);
		System.setProperty("carrot.config.repository.local", local);

		final Config conf = ConfigFactory.defaultReference().getConfig(
				"carrot.config.repository");

		log.debug("conf : {}", conf);

		repo = new RepoServiceImpl(conf);

		assertTrue(repo.deleteRepoAll());

	}

	@AfterClass
	public static void testDone() {

		assertTrue(repo.deleteRepoAll());

	}

	private void testRepo() {

		assertTrue(repo.ensureRepoAll());

		assertTrue(repo.updateRepoArchon());
		assertTrue(repo.updateRepoVersion());
		assertTrue(repo.updateRepoMaster("1.0.1"));

		//

		final File versionFile = new File(repo.getLocalVersion(),
				"/instance/com/company/karaf/version.conf");

		assertFile(versionFile);

		final Config versionConfig = ConfigFactory.parseFile(versionFile);
		log.info("version : \n{}", versionConfig);
		assertEquals(versionConfig.getString("carrot.config.version"), "1.0.2");

		//

		final File appFile = new File(repo.getLocalMaster(),
				"/instance/com/company/karaf/application.conf");
		assertFile(appFile);

		final Config appConfig = ConfigFactory.parseFile(appFile);
		log.info("application : \n{}", appConfig);
		assertEquals(appConfig.getString("main.name"), "app name");
		assertEquals(appConfig.getInt("main.size"), 123);

	}

	// first pass - clone & fetch
	@Test
	public void testRepo1() {
		testRepo();
	}

	// second pass - fetch only
	@Test
	public void testRepo2() {
		testRepo();
	}

}
