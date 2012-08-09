/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.karaf;

import static org.ops4j.pax.exam.CoreOptions.*;

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

import com.carrotgarden.conf.karaf.api.ConfigManager;

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

				systemTimeout(30 * 1000),

				felix(),

				junitBundles(),

				repository("http://repo1.maven.org/maven2").id("test-central"),

				repository(
						"http://repository.springsource.com/maven/bundles/release")
						.id("test-spring-release"),

				repository(
						"http://repository.springsource.com/maven/bundles/external")
						.id("test-spring-external"),

				mavenBundle().groupId("com.carrotgarden.osgi")
						.artifactId("carrot-osgi-anno-scr-core")
						.version("1.1.3"),

				mavenBundle().groupId("com.typesafe").artifactId("config")
						.version("0.5.0"),

				mavenBundle().groupId("org.apache.felix")
						.artifactId("org.apache.felix.scr").version("1.6.0"),

				mavenBundle().groupId("com.carrotgarden.conf")
						.artifactId("carrot-conf-base")
						.version("1.0.0-SNAPSHOT"),

				mavenBundle().groupId("com.carrotgarden.conf")
						.artifactId("carrot-conf-file")
						.version("1.0.0-SNAPSHOT"),

				mavenBundle().groupId("com.carrotgarden.wrap")
						.artifactId("carrot-wrap-jgit")
						.version("2.0.0-build000"),

				mavenBundle().groupId("org.apache.servicemix.bundles")
						.artifactId("org.apache.servicemix.bundles.jsch")
						.version("0.1.48_1"),

				mavenBundle().groupId("org.apache.sling")
						.artifactId("org.apache.sling.commons.scheduler")
						.version("2.3.4"),

				mavenBundle().groupId("org.apache.sling")
						.artifactId("org.apache.sling.commons.threads")
						.version("3.1.0"),

				mavenBundle().groupId("javax.servlet")
						.artifactId("com.springsource.javax.servlet")
						.version("2.5.0"),

				// mavenBundle().groupId("org.apache.karaf.system")
				// .artifactId("org.apache.karaf.system.core")
				// .version("3.0.0-SNAPSHOT"),

				bundle("reference:file:target/classes"),

				workingDirectory(System.getProperty("user.dir")
						+ "/target/tester")

		);

	}

	@Inject
	private BundleContext context;

	@Inject
	private ConfigManager configManager;

	@Test
	public void test() throws Exception {

		log.info("################################");

		log.info("### curren bundle " + context.getBundle().getSymbolicName());

		for (final Bundle bundle : context.getBundles()) {
			log.info("### active bundle : " + bundle.getSymbolicName());
		}

		Thread.sleep(10 * 1000);

		log.info("################################");

	}

}
