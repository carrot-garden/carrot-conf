/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.diff;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestConfigDiff {

	static final Logger log = LoggerFactory.getLogger(TestConfigDiff.class);

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

	private static DiffResultConfig getDiff(final String caseName) {

		final String caseDir = CASE_DIR_BASE + "/" + caseName;

		final File fileOLD = new File(caseDir, CASE_FILE_ONE);

		final Config confOLD = ConfigFactory.load(ConfigFactory
				.parseFile(fileOLD));

		log.debug("confOLD : {}", confOLD.getConfig(TEST_KEY));

		//

		final File fileNEW = new File(caseDir, CASE_FILE_TWO);

		final Config confNEW = ConfigFactory.load(ConfigFactory
				.parseFile(fileNEW));

		log.debug("confNEW : {}", confNEW.getConfig(TEST_KEY));

		//

		final DiffResultConfig confDiff = ConfigDiff.compare(confOLD, confNEW);

		log.debug("confDiff : {}", confDiff);

		return confDiff;

	}

	@Test
	public void test01() {

		final DiffResultConfig diff = getDiff("case-01");

		{

			assertFalse(diff.hasDelete());

		}

		{

			assertFalse(diff.hasInsert());

		}

		{

			assertTrue(diff.hasUpdateDiff());

			final Config from = ConfigFactory.parseString(//
					TEST_KEY + "{ timeout = 15s }");

			assertEquals(from, diff.getUpdateFrom());

			final Config into = ConfigFactory.parseString(//
					TEST_KEY + "{ timeout = 20s }");

			assertEquals(into, diff.getUpdateInto());

		}

	}

	@Test
	public void test02() {

		final DiffResultConfig diff = getDiff("case-02");

		{

			assertTrue(diff.hasUpdateDiff());

			final Config from = ConfigFactory.parseString(//
					TEST_KEY + "{ timeout = 15s }");

			assertEquals(from, diff.getUpdateFrom());

			final Config into = ConfigFactory.parseString(//
					TEST_KEY + "{ timeout = 20s }");

			assertEquals(into, diff.getUpdateInto());

		}

		{

			assertTrue(diff.hasDelete());

			final Config delete = ConfigFactory.parseString(//
					TEST_KEY + "{ list = [ { name=hello, value=world }] }");

			assertEquals(delete, diff.getDelete());

		}

		{

			assertTrue(diff.hasInsert());

			final Config insert = ConfigFactory.parseString(//
					TEST_KEY
							+ "{ list = [ { name=hello1, value=world1 }], comment = hello }");

			assertEquals(insert, diff.getInsert());

		}

		{

			final boolean hasSameList = diff.getUpdateNone().hasPath(
					TEST_KEY + ".list");

			assertFalse(hasSameList);

		}

	}

	@Test
	public void test0() {

		Config confONE;

		{

			final Properties props = new Properties();
			props.put("name", "hello");
			props.put("value", "123");

			confONE = ConfigFactory.parseProperties(props);

		}

		Config confTWO;

		{

			final Properties props = new Properties();
			props.put("value", "123");
			props.put("name", "hello");

			confTWO = ConfigFactory.parseProperties(props);

		}

		assertEquals(confONE, confTWO);

	}

}
