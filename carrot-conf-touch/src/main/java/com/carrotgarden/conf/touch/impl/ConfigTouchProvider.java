/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.touch.impl;

import java.io.File;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Property;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.event.ConfigEvent;
import com.carrotgarden.conf.sync.api.ConfigManager;
import com.carrotgarden.conf.touch.api.ConfigTouch;
import com.carrotgarden.osgi.conf.api.ConfigAdminService;

@Component( //
immediate = true, //
configurationPolicy = ConfigurationPolicy.REQUIRE, //
name = ConfigTouch.SERVICE_FACTORY_PID //
)
public class ConfigTouchProvider implements ConfigTouch, EventHandler {

	private final Logger log = LoggerFactory.getLogger(getClass());

	/** provide event subscription topic filter */
	@Property(name = EventConstants.EVENT_TOPIC)
	static final String TOPIC = ConfigEvent.CONFIG_CHANGE;

	private boolean isValid() {
		return manager.isIdentityValid() && manager.isConfigValid();
	}

	private Map<String, String> props;

	@Activate
	protected void activate(final Map<String, String> props) {

		this.props = props;

		log.debug("activate : {}", props);

		processChange();

	}

	@Modified
	protected void modified(final Map<String, String> props) {

		this.props = props;

		log.debug("modified : {}", props);

		processChange();

	}

	@Deactivate
	protected void deactivate(final Map<String, String> props) {

		this.props = props;

		log.debug("deactivate : {}", props);

	}

	private ConfigManager manager;

	@Reference
	protected void bind(final ConfigManager s) {
		manager = s;
	}

	protected void unbind(final ConfigManager s) {
		manager = null;
	}

	@Override
	public void handleEvent(final Event event) {

		log.debug("event : {}", event.getTopic());

		processChange();

	}

	private void processChange() {

		if (!isValid()) {
			log.debug("not valid; ignore");
			return;
		}

		if (props == null) {
			log.error("", new IllegalStateException("props == null"));
			return;
		}

		touchFile();

		touchService();

	}

	/** update time stamp */
	private void touchFile() {

		final String touch = props.get(KEY_FILE_TOUCH);

		log.debug("file : {}={}", KEY_FILE_TOUCH, touch);

		if (!"true".equalsIgnoreCase(touch)) {
			return;
		}

		final String name = props.get(KEY_FILE_NAME);

		log.debug("file : {}={}", KEY_FILE_NAME, name);

		if (name == null) {
			log.error("missing file name", new IllegalArgumentException(
					KEY_FILE_NAME));
			return;
		}

		final File file = new File(name);

		final String path = file.getAbsolutePath();

		if (!file.exists() || !file.canRead() || !file.canWrite()) {
			log.error("invalid file", new IllegalStateException(path));
			return;
		}

		file.setLastModified(System.currentTimeMillis());

		log.debug("touch file : \n\t path={}", path);

	}

	/** merge properties and update target service */
	private void touchService() {

		final String touch = props.get(KEY_SERVICE_TOUCH);

		log.debug("service : {}={}", KEY_SERVICE_TOUCH, touch);

		if (!"true".equalsIgnoreCase(touch)) {
			return;
		}

		final String servicePid = props.get(KEY_SERVICE_PID);

		log.debug("service : {}={}", KEY_SERVICE_PID, servicePid);

		if (servicePid == null) {
			log.error("", new IllegalArgumentException("servicePid == null"));
			return;
		}

		final String servicePropsText = props.get(KEY_SERVICE_PROPS);

		final Properties servicePropsRaw = new Properties();

		try {
			if (servicePropsText != null) {
				servicePropsRaw.load(new StringReader(servicePropsText));
			}
		} catch (final Exception e) {
			log.error("failed to load properties", e);
			return;
		}

		/** load existing service configuration */
		final Map<String, String> servicePropsMap = configAdmin
				.properties(servicePid);

		for (final Map.Entry<?, ?> entry : servicePropsRaw.entrySet()) {
			final String key = "" + entry.getKey();
			final String value = "" + entry.getValue();
			servicePropsMap.put(key, value);
		}

		log.debug("touch service: \n\t pid={}  \n\t properties={}", //
				servicePid, servicePropsMap);

		/** apply updated service configuration */
		configAdmin.singletonUpdate(servicePid, servicePropsMap);

	}

	private ConfigAdminService configAdmin;

	@Reference
	protected void bind(final ConfigAdminService s) {
		configAdmin = s;
	}

	protected void unbind(final ConfigAdminService s) {
		configAdmin = null;
	}

}
