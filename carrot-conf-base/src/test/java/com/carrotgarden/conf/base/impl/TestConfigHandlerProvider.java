package com.carrotgarden.conf.base.impl;

import static org.testng.AssertJUnit.*;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import util.JDK;

import com.carrotgarden.conf.base.api.ConfigConst;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestConfigHandlerProvider {

	private static final Logger log = LoggerFactory
			.getLogger(TestConfigHandlerProvider.class);

	private final String protocol = ConfigHandlerProvider.PROTOCOL;
	private final ConfigHandlerProvider handler = new ConfigHandlerProvider();

	final IdentityServiceProvider identityService = new IdentityServiceProvider();

	final ConfigServiceProvider configService = new ConfigServiceProvider();

	/** emulate osgi setup */
	@BeforeTest
	protected void setUp() throws Exception {

		System.setProperty(ConfigConst.Id.SYSTEM_PROPERTY, "karaf.company.com");

		configService.bind(identityService);
		configService.activate();

		handler.bind(configService);
		handler.activate();

		JDK.handlerAdd(protocol, handler);

	}

	@AfterTest
	protected void tearDown() throws Exception {

		JDK.handlerRemove(protocol);

	}

	@Test
	public void testMakeURL() throws Exception {

		final URL url = new URL("config:/instance/application.conf");

		assertEquals(url.getProtocol(), "config");
		assertEquals(url.getHost(), "");
		assertEquals(url.getPort(), -1);
		assertEquals(url.getPath(), "/instance/application.conf");
		assertEquals(url.getFile(), "/instance/application.conf");

	}

	@Test
	public void testReadURL() throws Exception {

		final URL url = new URL(
				"config:/instance/com/company/karaf/application.conf");

		final Config conf = ConfigFactory.parseURL(url);

		log.debug("conf : \n{}", conf);

		assertEquals(conf.getString("main.name"), "app name");
		assertEquals(conf.getInt("main.size"), 123);

	}

}
