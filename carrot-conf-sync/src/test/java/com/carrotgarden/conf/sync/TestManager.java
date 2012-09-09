/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.sync;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.event.ConfigEvent;
import com.carrotgarden.conf.sync.api.ConfigManager;
import com.carrotgarden.conf.sync.impl.Util;
import com.carrotgarden.conf.util.ConfigAny;
import com.carrotgarden.osgi.event.api.EventUtil;
import com.typesafe.config.Config;

@RunWith(JUnit4TestRunner.class)
public class TestManager extends TestAny implements EventHandler {

	private final static Logger log = LoggerFactory
			.getLogger(TestManager.class);

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
	private ConfigManager configManager;

	@SuppressWarnings("rawtypes")
	@Test
	public void test() throws Exception {

		final Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put(EventConstants.EVENT_TOPIC, ConfigEvent.ALL);

		final ServiceRegistration reg = context.registerService(
				EventHandler.class.getName(), this, props);

		Thread.sleep(20 * 1000);

		assertEquals(countConfigChange, 1);
		assertTrue(countVersionSuccess >= 3);
		assertEquals(countVersionFailure, 0);

		assertTrue(configManager.isIdentityValid());
		assertTrue(configManager.isConfigValid());

		final Config config = configManager.getConfig();

		log.info(" config : \n{}", ConfigAny.toString(config));

		reg.unregister();

	}

	private int countConfigChange;
	private int countVersionSuccess;
	private int countVersionFailure;

	@Override
	public void handleEvent(final Event event) {

		log.info("event topic : {}", event.getTopic());

		if (EventUtil.is(event, ConfigEvent.CONFIG_CHANGE)) {
			countConfigChange++;
		}

		if (EventUtil.is(event, ConfigEvent.JOB_VERSION_SUCCESS)) {
			countVersionSuccess++;
		}

		if (EventUtil.is(event, ConfigEvent.JOB_VERSION_FAILURE)) {
			countVersionFailure++;
		}

	}

}
