/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.repo.impl;

import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.*;

import java.net.URL;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import util.JDK;

import com.carrotgarden.conf.id.api.Identity;
import com.carrotgarden.conf.id.api.Identity.Source;
import com.carrotgarden.conf.id.api.IdentityService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestConfigHandlerProvider {

	private static final Logger log = LoggerFactory
			.getLogger(TestConfigHandlerProvider.class);

	private static final String protocol = ConfigHandlerProvider.PROTOCOL;

	private static final String id = "karaf.company.com";

	private static final String local = "./target/"
			+ TestConfigHandlerProvider.class.getSimpleName() + "-"
			+ UUID.randomUUID().toString();

	/** emulate osgi setup */
	@BeforeClass
	public static void testInit() throws Exception {

		System.setProperty("carrot.config.identity", id);
		System.setProperty("carrot.config.repository.local", local);

		final Identity identity = mock(Identity.class);
		when(identity.getId()).thenReturn(id);
		when(identity.isValid()).thenReturn(true);
		when(identity.getSource()).thenReturn(Source.SYSTEM_PROPERTY);

		final IdentityService identityService = mock(IdentityService.class);
		when(identityService.getCurrentIdentity()).thenReturn(identity);

		final ConfigServiceProvider configService = new ConfigServiceProvider();

		configService.bind(identityService);
		configService.activate();

		final ConfigHandlerProvider handler = new ConfigHandlerProvider();
		handler.bind(configService);
		handler.activate();

		JDK.handlerAdd(protocol, handler);

		configService.updateIdentity();
		configService.updateVersion();
		configService.updateMaster();

	}

	@AfterClass
	protected void testDone() throws Exception {

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
