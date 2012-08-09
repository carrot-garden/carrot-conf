/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestConfigHazel {

	private static final Logger log = LoggerFactory
			.getLogger(TestConfigHazel.class);

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	@Test
	public void testEmpty() {

		final Config typeConf = ConfigFactory.empty();

		final com.hazelcast.config.Config hazelConf = ConfigHazel
				.hazelFrom(typeConf);

		log.debug("empty : \n{}", ConfigHazel.toStringXML(hazelConf));
		log.debug("empty : \n{}", ConfigHazel.toStringJSON(hazelConf));

	}

	@Test
	public void test() {

		final Config typeConf = ConfigFactory.load("case-01/hazel.conf");

		final Config hazelcast = typeConf.getConfig("hazelcast");

		// log.debug("hazelcast : {}", hazelcast);

		final com.hazelcast.config.Config hazelConf = ConfigHazel
				.hazelFrom(hazelcast.root());

		log.debug("hazelConf : \n{}", ConfigHazel.toStringXML(hazelConf));
		log.debug("hazelConf : \n{}", ConfigHazel.toStringJSON(hazelConf));

		//

		assertEquals(hazelConf.getGroupConfig().getName(), "group-name");
		assertEquals(hazelConf.getGroupConfig().getPassword(), "group-password");

		//

		assertEquals(hazelConf.getNetworkConfig().getJoin().getTcpIpConfig()
				.isEnabled(), true);

		assertEquals(hazelConf.getNetworkConfig().getJoin()
				.getMulticastConfig().getMulticastGroup(), "224.1.2.3");

		assertEquals(hazelConf.getPort(), 57012);
		assertEquals(hazelConf.isPortAutoIncrement(), true);

	}

}
