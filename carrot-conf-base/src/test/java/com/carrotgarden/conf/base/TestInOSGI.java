package com.carrotgarden.conf.base;

import static junit.framework.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.net.URL;

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

import com.carrotgarden.conf.base.api.ConfigConst;
import com.carrotgarden.conf.base.api.ConfigService;
import com.typesafe.config.Config;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class TestInOSGI {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	static final String ID = "karaf.company.com";

	@Configuration
	public Option[] config() {

		return options(

				systemProperty(ConfigConst.Id.SYSTEM_PROPERTY).value(ID),

				felix(),

				junitBundles(),

				repository("http://repo1.maven.org/maven2").id("central"),
				repository("http://download.eclipse.org/jgit/maven").id("jgit"),

				mavenBundle().groupId("com.carrotgarden.osgi")
						.artifactId("carrot-osgi-anno-scr-core")
						.version("1.1.1"),

				mavenBundle().groupId("com.typesafe").artifactId("config")
						.version("0.5.0"),

				mavenBundle().groupId("org.apache.felix")
						.artifactId("org.apache.felix.scr").version("1.6.0"),

				mavenBundle().groupId("org.eclipse.jgit")
						.artifactId("org.eclipse.jgit")
						.version("1.3.0.201202151440-r"),

				mavenBundle().groupId("org.openengsb.wrapped")
						.artifactId("com.jcraft.jsch-all")
						.version("1.0.6.RELEASE"),

				bundle("reference:file:target/classes"), // this project

				workingDirectory(System.getProperty("user.dir")
						+ "/target/tester")

		);

	}

	@Inject
	private BundleContext context;

	@Inject
	private ConfigService configService; // wait for service

	@Test
	public void testContext() throws Exception {

		log.info("################################");

		log.info("### curren bundle " + context.getBundle().getSymbolicName());

		for (final Bundle bundle : context.getBundles()) {
			log.info("### active bundle : " + bundle.getSymbolicName());
		}

		final URL confURL = new URL("config:");

		log.info("### confURL : " + confURL);

		assertTrue(configService.isConfigAvailable());

		final Config conf = configService.getInstanceConfig();

		assertEquals(conf.getString("main.name"), "app name");
		assertEquals(conf.getInt("main.size"), 123);

		log.error("################################");

	}

}
