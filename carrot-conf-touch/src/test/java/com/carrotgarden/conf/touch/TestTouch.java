/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.touch;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.event.ConfigEvent;
import com.carrotgarden.conf.sync.api.ConfigManager;
import com.carrotgarden.conf.sync.impl.Util;
import com.carrotgarden.conf.touch.api.ConfigTouch;
import com.carrotgarden.osgi.conf.api.ConfigAdminService;

@RunWith(JUnit4TestRunner.class)
public class TestTouch extends TestAny implements EventHandler {

	private final static Logger log = LoggerFactory.getLogger(TestTouch.class);

	private final String id = "karaf.company.com";

	private final String local = "./target/" + getClass().getSimpleName() + "-"
			+ UUID.randomUUID().toString();

	@Override
	@Configuration
	public Option[] config() {

		return Util.concat(

				super.config(),

				options(systemProperty("carrot.config.identity").value(id),
						systemProperty("carrot.config.repository.local").value(
								local)

				));

	}

	@Inject
	private BundleContext context;

	@Inject
	private EventAdmin eventAdmin;

	@Inject
	private ConfigAdminService configAdmin;

	@Inject
	private ConfigManager manager;

	static final String PAX_PID = "org.ops4j.pax.logging";

	static final String PAX_FILTER = //
	"(" + Constants.SERVICE_PID + "=" + PAX_PID + ")";

	@Test
	public void test() throws Exception {

		assertNotNull(configAdmin);

		assertNotNull(eventAdmin);

		assertNotNull(manager);

		final Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put(EventConstants.EVENT_TOPIC, ConfigEvent.ALL);

		@SuppressWarnings("rawtypes")
		final ServiceRegistration reg = context.registerService(
				EventHandler.class.getName(), this, props);

		{

			// configurePaxLogging(context);

			final Map<String, String> loggingProps = new HashMap<String, String>();

			loggingProps.put("log4j.rootLogger", "INFO");

			// final boolean isDone = configAdmin.singletonUpdate(
			// PAX_LOGGING_SERVICE_PID, loggingProps);
			//
			// assertTrue(isDone);

		}

		{

			final Map<String, String> touchProps = new HashMap<String, String>();

			touchProps.put(ConfigTouch.KEY_SERVICE_TOUCH, "true");
			touchProps.put(ConfigTouch.KEY_SERVICE_PID, PAX_PID);

			final String pid = configAdmin.multitonCreate(
					ConfigTouch.SERVICE_FACTORY_PID, touchProps);

			log.debug("### touch service pid : {}", pid);

			Thread.sleep(200);

		}

		{

			final File file = new File(
					"./target/test-classes/org.ops4j.pax.logging.cfg");

			final Map<String, String> touchProps = new HashMap<String, String>();

			touchProps.put(ConfigTouch.KEY_FILE_TOUCH, "true");
			touchProps.put(ConfigTouch.KEY_FILE_NAME, file.getAbsolutePath());

			final String pid = configAdmin.multitonCreate(
					ConfigTouch.SERVICE_FACTORY_PID, touchProps);

			log.debug("### touch file pid : {}", pid);

			Thread.sleep(200);

		}

		Thread.sleep(5 * 1000);

		reg.unregister();

	}

	@Override
	public void handleEvent(final Event event) {

		log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

		log.info("event : {}", event.getTopic());

		log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	void configurePaxLogging(final BundleContext context) throws Exception {

		final ServiceReference[] referenceArray = context.getServiceReferences(
				ManagedService.class.getName(), PAX_FILTER);

		assertNotNull(referenceArray);
		assertEquals(1, referenceArray.length);

		final ServiceReference reference = referenceArray[0];

		// *** Configure pax-logging

		// Load log4j properties
		final Properties properties = new Properties();

		final URL resource = TestTouch.class
				.getResource("/org.ops4j.pax.logging.cfg");
		final InputStream propertiesStream = resource.openStream();
		properties.load(propertiesStream);

		// Update configuration
		final ManagedService service = (ManagedService) context
				.getService(reference);

		service.updated(properties);

		context.ungetService(reference);

	}

}
