/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.base;

import static junit.framework.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.net.URL;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.base.api.ConfigService;
import com.typesafe.config.Config;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class TestOSGI {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private final String id = "karaf.company.com";

	private final String local = "./target/" + getClass().getSimpleName() + "-"
			+ UUID.randomUUID().toString();

	@Configuration
	public Option[] config() {

		return options(

				systemProperty("carrot.config.identity").value(id),
				systemProperty("carrot.config.repository.local").value(local),

				felix(),

				junitBundles(),

				repository("http://repo1.maven.org/maven2").id("test-central"),

				mavenBundle().groupId("com.carrotgarden.osgi")
						.artifactId("carrot-osgi-anno-scr-core")
						.version("1.1.3"),

				mavenBundle().groupId("com.typesafe").artifactId("config")
						.version("0.5.0"),

				mavenBundle().groupId("org.apache.felix")
						.artifactId("org.apache.felix.scr").version("1.6.0"),

				mavenBundle().groupId("com.carrotgarden.wrap")
						.artifactId("carrot-wrap-jgit")
						.version("2.0.0-build000"),

				mavenBundle().groupId("org.apache.servicemix.bundles")
						.artifactId("org.apache.servicemix.bundles.jsch")
						.version("0.1.48_1"),

				bundle("reference:file:target/classes"),

				workingDirectory(System.getProperty("user.dir")
						+ "/target/tester")

		);

	}

	@Inject
	private BundleContext context;

	@Inject
	private ConfigService configService;

	@Test
	public void test() throws Exception {

		log.info("################################");

		log.info("### curren bundle " + context.getBundle().getSymbolicName());

		for (final Bundle bundle : context.getBundles()) {
			log.info("### active bundle : " + bundle.getSymbolicName());
		}

		final URL confURL = new URL("config:/instance/application.conf");

		log.info("### confURL : " + confURL);

		//

		// configService.updateIdentity();
		// configService.updateVersion();
		// configService.updateMaster();

		assertTrue(configService.isMasterValid());

		final Config config = configService.getMasterConfig();

		log.info("### config : " + config);

		log.info("### identity : {}", configService.getIdentity());

		log.info("### version root : {}", configService.getVersionRoot());
		log.info("### version instance : {}",
				configService.getVersionInstance());

		log.info("### master root : {}", configService.getMasterRoot());
		log.info("### master instance : {}", configService.getMasterInstance());

		assertEquals(config.getString("main.name"), "app name");
		assertEquals(config.getInt("main.size"), 123);

		log.error("################################");

	}

}
