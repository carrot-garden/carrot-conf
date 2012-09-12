/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.repo.api;

import static org.testng.AssertJUnit.*;
import static org.testng.FileAssert.*;

import java.io.File;
import java.util.UUID;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.carrotgarden.conf.repo.impl.RepoService;
import com.carrotgarden.conf.repo.impl.RepoServiceImpl;
import com.carrotgarden.conf.repo.impl.Util;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestRepoVersionChange {

	private static final Logger log = LoggerFactory
			.getLogger(TestRepoVersionChange.class);

	private static final String id = "karaf.company.com";

	private static final String local = "./target/"
			+ TestRepoVersionChange.class.getSimpleName() + "-"
			+ UUID.randomUUID().toString();

	private static RepoService repo;

	@BeforeClass
	public static void testInit() {

		System.setProperty("carrot.config.identity", id);
		System.setProperty("carrot.config.repository.local", local);

		//

		final Config conf = ConfigFactory.defaultReference().getConfig(
				"carrot.config.repository");

		log.debug("conf : {}", conf);

		repo = new RepoServiceImpl(conf);

		assertTrue(repo.deleteRepoAll());

		assertTrue(repo.ensureRepoAll());
		assertTrue(repo.updateRepoArchon());

	}

	@AfterClass
	public static void testDone() {

		assertTrue(repo.deleteRepoAll());

	}

	static final String PATH = "instance/com/company/karaf/version.conf";

	private void editArchonVersion(final String versionOld,
			final String versionNew) throws Exception {

		final File local = new File("./target/test-repo" + "."
				+ UUID.randomUUID());

		final String remote = repo.getLocalArchon().toURI().toString();

		final String branch = "refs/heads/version";

		log.debug("local : {}", local);

		final CloneCommand clone = Git.cloneRepository();
		clone.setBranch(branch);
		clone.setDirectory(local);
		clone.setURI(remote);
		clone.call();

		//

		final File file = new File(local, PATH);
		assertFile(file);

		final String confIn = Util.loadFileAsString(file.getAbsolutePath());
		log.debug("confIn : \n{}", confIn);

		final String confOut = confIn.replace(versionOld, versionNew);
		log.debug("confOut : \n{}", confOut);

		Util.saveFileAsString(file.getAbsolutePath(), confOut);

		//

		final Git git = Git.open(local);

		git.add().addFilepattern(PATH).call();

		git.commit().setMessage(versionOld + "->" + versionNew).call();

		git.push().call();

		assertTrue(Util.deleteFiles(local));

	}

	private void testRepoVersion(final String versionOld,
			final String versionNew) throws Exception {

		final String propName = Util.constant().keyVersion();

		editArchonVersion(versionOld, versionNew);

		assertTrue(repo.updateRepoVersion());

		final File versionFile = new File(repo.getLocalVersion(), PATH);
		assertFile(versionFile);

		final Config versionConfig = ConfigFactory.parseFile(versionFile);
		log.info("version : \n{}", versionConfig);
		assertEquals(versionConfig.getString(propName), versionNew);

	}

	@Test
	public void testRepoVersion1() throws Exception {
		testRepoVersion("1.0.7", "1.1.1");
	}

	@Test
	public void testRepoVersion2() throws Exception {
		testRepoVersion("1.1.1", "2.2.2");
	}

}
