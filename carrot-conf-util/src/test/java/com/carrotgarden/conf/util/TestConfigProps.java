/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.util;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestConfigProps {

	private static final Logger log = LoggerFactory
			.getLogger(TestConfigProps.class);

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	@Test
	public void testProps() {

		final Config conf = ConfigFactory.parseString(//
				"tester = {" + //
						"number = 123 ," + //
						"boolean = true ," + //
						"string = hello ," + //
						"}" + //
						"");

		final Properties props = ConfigProps
				.propsFrom(conf.getConfig("tester"));

		log.debug("props : {}", props);

		assertEquals(props.get("number"), 123);
		assertEquals(props.get("boolean"), true);
		assertEquals(props.get("string"), "hello");

	}

	@Test
	public void testXdoc() {

	}

}
