package com.carrotgarden.conf.base.impl;

import static org.testng.AssertJUnit.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.carrotgarden.conf.base.api.ConfigConst;
import com.carrotgarden.conf.base.api.Identity;
import com.carrotgarden.conf.base.api.IdentitySource;

import util.JDK;

public class TestIdentityFromAmazonEC2 {

	private final static Logger log = LoggerFactory
			.getLogger(TestIdentityFromAmazonEC2.class);

	private final String value = UUID.randomUUID().toString();

	private Server server;

	class UserDataHandler extends AbstractHandler {

		@Override
		public void handle(final String target, final Request baseRequest,
				final HttpServletRequest request,
				final HttpServletResponse response) throws IOException,
				ServletException {

			final String text = //
			ConfigConst.Key.INSTANCE + " = " + "\"" + value + "\"";

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

	private void hackURL() throws Exception {

		final String original = ConfigConst.Id.AMAZON_EC2_URL;

		final URL good = new URL(original);

		final String host = getHost();
		final int port = getPort();

		final URL hack = new URL(good.getProtocol(), host, port, good.getFile());

		final Field field = ConfigConst.Id.class.getField("AMAZON_EC2_URL");

		JDK.setHiddenFiled(field, null, hack.toString());

		final String replacement = ConfigConst.Id.AMAZON_EC2_URL;

		log.debug("original    = " + original);
		log.debug("replacement = " + replacement);

	}

	@BeforeTest
	protected void setUp() throws Exception {

		hackURL();

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
	public void testIdentity() {

		final Identity id0 = IdentitySource.AMAZON_EC2.newIdentity();
		assertTrue(id0.isAvailable());
		assertEquals(id0.getId(), value);

		log.debug("#########################");

		log.debug("id = " + id0.getId());

		log.debug("#########################");

	}

}
