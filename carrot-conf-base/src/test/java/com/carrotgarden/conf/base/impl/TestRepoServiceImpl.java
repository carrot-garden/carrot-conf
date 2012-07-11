/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.base.impl;

import static org.testng.AssertJUnit.*;
import static org.testng.FileAssert.*;

import java.io.File;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.carrotgarden.conf.base.api.ConfigConst;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestRepoServiceImpl {

	private static final Logger log = LoggerFactory
			.getLogger(TestRepoServiceImpl.class);

	private RepoService repo;

	@BeforeTest
	public void testBegin() {

		final String local = "./target/config-repo-service-"
				+ UUID.randomUUID();

		final Properties properties = new Properties();
		properties.put("local", local);

		final Config prop = ConfigFactory.parseProperties(properties);

		//

		final Config boot = ConfigFactory.load(ConfigConst.Repo.BOOT_FILE);
		final Config tree = boot.getConfig(ConfigConst.Key.REPOSITORY);
		final Config conf = prop.withFallback(tree);

		repo = new RepoServiceImpl(conf);

		assertTrue(repo.deleteRepoAll());

	}

	@AfterTest
	public void testEnd() {

		assertTrue(repo.deleteRepoAll());

	}

	private void testRepoMaster(final String version) {

		assertTrue(repo.ensureRepoAll());

		assertTrue(repo.updateRepoArchon());
		assertTrue(repo.updateRepoMaster(version));

		//

		final File appFile = new File(repo.getLocalMaster(),
				"/instance/com/company/karaf/application.conf");
		assertFile(appFile);

		final Config appConfig = ConfigFactory.parseFile(appFile);
		log.info("application : \n{}", appConfig);
		assertEquals(appConfig.getString("main.name"), "app name");
		assertEquals(appConfig.getInt("main.size"), 123);
		assertEquals(appConfig.getString("main.version"), version);

	}

	@Test
	public void testRepoVersion() {

		assertTrue(repo.ensureRepoAll());

		assertTrue(repo.updateRepoArchon());
		assertTrue(repo.updateRepoVersion());

		final File versionFile = new File(repo.getLocalVersion(),
				"/instance/com/company/karaf/version.conf");
		assertFile(versionFile);

		final Config versionConfig = ConfigFactory.parseFile(versionFile);
		log.info("version : \n{}", versionConfig);
		assertEquals(versionConfig.getString(ConfigConst.Key.VERSION), "1.0.1");

	}

	// first pass - clone & fetch
	@Test
	public void testRepo1() {
		testRepoMaster("1.0.1");
	}

	// second pass - fetch only
	@Test
	public void testRepo2() {
		testRepoMaster("1.0.1");
	}

	// second pass - fetch new version
	@Test
	public void testRepo3() {
		testRepoMaster("1.0.2");
	}

}
