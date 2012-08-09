/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.entry;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestConfigEntry {

	private static final Logger log = LoggerFactory
			.getLogger(TestConfigEntry.class);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {

		final Config config = ConfigFactory.load("case-01/one.conf");

		final ConfigEntry entry = new ConfigEntry(
				config.getConfig("carrot.config.repository"));

		log.debug("entry : {}", entry);

		final List<Map<String, Object>> list = entry.load("point_list");

		log.debug("list : {}", list);

		final Map<String, Object> item = list.get(0);

		log.debug("item : {}", item);

	}

}
