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
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestUtil {

	private static final Logger log = LoggerFactory.getLogger(TestUtil.class);

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	static final String ROOT = "/instance";

	@Test
	public void testFileAsString() throws Exception {

		final String name = UUID.randomUUID().toString();

		final String file = "./target/" + name;

		final String textSave = UUID.randomUUID().toString() + "\n";

		Util.saveFileAsString(file, textSave);

		final String textLoad = Util.loadFileAsString(file);

		new File(file).delete();

		assertEquals(textSave, textLoad);

	}

	@Test
	public void testUrlAsString() throws Exception {

		final String host = "www.google.com";

		final String url = "http://" + host;

		final String text = Util.loadUrlAsString(url);

		assertTrue(text.contains(host));

	}

	@Test
	public void testOverride() {

		final String propName = Util.constValues().keyRepository();

		final Config root = ConfigFactory.defaultReference();

		final Config tree = root.getConfig(propName);

		final Properties properties = new Properties();
		properties.put("local", "override");

		final Config prop = ConfigFactory.parseProperties(properties);

		final Config conf = prop.withFallback(tree);

		log.debug("conf : {}", conf);

		assertEquals(conf.getString("local"), "override");

	}

	@Test
	public void testPath() {

		assertEquals(Util.instancePathFromInstanceId(ROOT, null), ROOT);

		assertEquals(Util.instancePathFromInstanceId(ROOT, ""), ROOT);

		assertEquals(
				Util.instancePathFromInstanceId(ROOT, "karaf-company-com"),
				ROOT + "/karaf-company-com");

		assertEquals(
				Util.instancePathFromInstanceId(ROOT, "karaf.company.com"),
				ROOT + "/com/company/karaf");

		assertEquals(
				Util.instancePathFromInstanceId(ROOT, "123.karaf.company.com"),
				ROOT + "/com/company/karaf/123");

	}

	@Test
	public void testTrim() {

		assertEquals(Util.instancePathTrimLast(ROOT, ROOT), ROOT);

		assertEquals(Util.instancePathTrimLast(ROOT, ROOT + "/trim"), ROOT);

		assertEquals(Util.instancePathTrimLast(ROOT, ROOT + "/trim/more"), ROOT
				+ "/trim");

	}

}
