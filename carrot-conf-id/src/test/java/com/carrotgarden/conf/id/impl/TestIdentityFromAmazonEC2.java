/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.id.impl;

import static org.testng.AssertJUnit.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.carrotgarden.conf.id.api.Identity;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

public class TestIdentityFromAmazonEC2 {

	private final static Logger log = LoggerFactory
			.getLogger(TestIdentityFromAmazonEC2.class);

	@BeforeClass
	public static void testInit() {

	}

	private final String value = UUID.randomUUID().toString();

	private Server server;

	class UserDataHandler extends AbstractHandler {

		final String propName = Util.constant().keyIdentity();

		@Override
		public void handle(final String target, final Request baseRequest,
				final HttpServletRequest request,
				final HttpServletResponse response) throws IOException,
				ServletException {

			final String text = propName + " = " + "\"" + value + "\"";

			response.getWriter().println(text);

			response.setContentType("text/plain;charset=utf-8");

			response.setStatus(HttpServletResponse.SC_OK);

			baseRequest.setHandled(true);

		}

	}

	private String getHost() {
		return "0.0.0.0";
	}

	// FIXME provide available port only
	private int getPort() {
		return 8123;
	}

	private Config hackURL(final Config source) throws Exception {

		final String path = "id.amazon-url-ec2";

		final String original = source.getString(path);

		final URL good = new URL(original);

		final String host = getHost();
		final int port = getPort();

		final URL hack = new URL(good.getProtocol(), host, port, good.getFile());

		final Config target = source.withValue(path,
				ConfigValueFactory.fromAnyRef(hack.toString()));

		final String replacement = target.getString(path);

		log.debug("original    = " + original);
		log.debug("replacement = " + replacement);

		return target;

	}

	@BeforeTest
	protected void setUp() throws Exception {

		final InetSocketAddress addr = //
		new InetSocketAddress(getHost(), getPort());

		server = new Server(addr);

		server.setHandler(new UserDataHandler());

		server.start();

	}

	@AfterTest
	protected void tearDown() throws Exception {

		server.stop();

	}

	@Test
	public void testIdentity() throws Exception {

		final Config configIn = ConfigFactory.defaultReference().getConfig(
				"carrot.config.const");

		final Config configOut = hackURL(configIn);

		final Constant constValues = new Constant(configOut);

		final Identity id0 = new IdentityFromAmazonEC2(constValues);

		assertTrue(id0.isAvailable());
		assertEquals(id0.getId(), value);

		log.debug("#########################");

		log.debug("id = " + id0.getId());

		log.debug("#########################");

	}

}
