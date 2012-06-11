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

public class TestRepoChangeMaster {

	private static final Logger log = LoggerFactory
			.getLogger(TestRepoChangeMaster.class);

	private RepoService repo;

	@BeforeTest
	public void testBegin() {

		final String local = "./target/config-change-master-"
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

		assertTrue(repo.ensureRepoAll());
		assertTrue(repo.updateRepoArchon());

	}

	static final String PATH = "instance/com/company/karaf/application.conf";

	@AfterTest
	public void testEnd() {

		assertTrue(repo.deleteRepoAll());

	}

	private void testRepoMaster(final String version) {

		assertTrue(repo.updateRepoMaster(version));

		final File appFile = new File(repo.getLocalMaster(), PATH);
		assertFile(appFile);

		final Config appConfig = ConfigFactory.parseFile(appFile);
		log.info("appConfig : \n{}", appConfig);
		assertEquals(appConfig.getString("main.version"), version);

	}

	@Test
	public void testRepoMaster1() {
		testRepoMaster("1.0.2");
	}

	@Test
	public void testRepoMaster2() {
		testRepoMaster("1.0.3");
	}

}
