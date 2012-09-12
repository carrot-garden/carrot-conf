/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.repo;

import static junit.framework.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.net.URL;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.repo.api.ConfigService;
import com.carrotgarden.conf.repo.impl.Util;
import com.typesafe.config.Config;

@RunWith(JUnit4TestRunner.class)
public class TestRepo extends TestAny {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private final String id = "karaf.company.com";

	private final String local = "./target/" + getClass().getSimpleName() + "-"
			+ UUID.randomUUID().toString();

	@Override
	@Configuration
	public Option[] config() {

		return Util.concat(

		super.config(),

		options(

		systemProperty("carrot.config.identity").value(id),

		systemProperty("carrot.config.repository.local").value(local)

		));

	}

	@Inject
	private BundleContext context;

	@Inject
	private ConfigService configService;

	@Test
	public void test() throws Exception {

		{

			final boolean isReady = true //
					&& configService.updateIdentity() //
					&& configService.updateVersion() //
					&& configService.updateMaster() //
			;

			assertTrue(isReady);

		}

		{
			final URL confURL = new URL("config:/instance/application.conf");

			log.info("### confURL : " + confURL);

		}

		{

			assertTrue(configService.isIdentityValid());
			assertTrue(configService.isVersionValid());
			assertTrue(configService.isMasterValid());

		}

		final Config config = configService.getMasterConfig();

		log.info("### config : " + config);

		log.info("### identity : {}", configService.getIdentity());

		log.info("### version root     : {}", configService.getVersionRoot());
		log.info("### version instance : {}",
				configService.getVersionInstance());

		log.info("### master root      : {}", configService.getMasterRoot());
		log.info("### master instance  : {}", configService.getMasterInstance());

		assertEquals(config.getString("main.name"), "app name");
		assertEquals(config.getString("main.version"), "1.0.6");
		assertEquals(config.getInt("main.size"), 123);

	}

}
