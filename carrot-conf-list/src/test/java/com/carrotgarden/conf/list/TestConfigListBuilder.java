/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.list;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestConfigListBuilder {

	static final Logger log = LoggerFactory
			.getLogger(TestConfigListBuilder.class);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	private static final String TEST_KEY = "carrot.config.repository";

	private static final String CASE_DIR_BASE = "src/test/resources";
	private static final String CASE_FILE_ONE = "one.conf";
	private static final String CASE_FILE_TWO = "two.conf";

	private static void assertLister(final String caseName) {

		final String caseDir = CASE_DIR_BASE + "/" + caseName;

		final File fileONE = new File(caseDir, CASE_FILE_ONE);

		final Config confONE = ConfigFactory.load(ConfigFactory
				.parseFile(fileONE));

		log.debug("confONE : {}", confONE.getConfig(TEST_KEY));

		//

		final Config confNEW = ConfigListBuilder.process(confONE);

		log.debug("confNEW : {}", confNEW.getConfig(TEST_KEY));

		//

		final File fileTWO = new File(caseDir, CASE_FILE_TWO);

		final Config confTWO = ConfigFactory.load(ConfigFactory
				.parseFile(fileTWO));

		log.debug("confTWO : {}", confTWO.getConfig(TEST_KEY));

		assertEquals(confTWO, confNEW);

	}

	@Test
	public void test01() {

		assertLister("case-01");

	}

	@Test
	public void test02() {

		assertLister("case-02");

	}

}
