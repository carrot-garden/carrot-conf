/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.util;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.typesafe.config.ConfigObject;

/**  */
public class ConfigHazel {

	public static final String EMPTY_CONFIG = "hazelcast-empty-config.xml";

	private static final Logger log = LoggerFactory
			.getLogger(ConfigHazel.class);

	public static Config hazelFrom(final com.typesafe.config.Config config) {
		return hazelFrom(config.root());
	}

	public static Config hazelFrom(final ConfigObject object) {

		try {

			final Document xdoc = ConfigXML.builder().newDocument();

			final Element element = xdoc.createElement("hazelcast");

			xdoc.appendChild(element);

			ConfigXML.applyConfig(object, xdoc, element);

			final InputStream input = ConfigHazel.class.getClassLoader()
					.getResourceAsStream(EMPTY_CONFIG);

			final XmlConfigBuilder builder = new XmlConfigBuilder(input);

			final Config hazel = builder.build(element);

			hazel.setXmlConfig(ConfigXML.toString(xdoc));

			return hazel;

		} catch (final Exception e) {

			log.error("failed to process", e);

			final Config hazel = new Config();

			/** clear hazel defaults */
			hazel.getNetworkConfig().getJoin().getMulticastConfig()
					.setEnabled(false);

			return hazel;

		}

	}

	public static String toStringJSON(final Config config) {

		final String text = config.getXmlConfig();

		if (text == null) {
			log.debug("invalid xml text", new Exception());
			return "{}";
		}

		try {

			final Document xdoc = ConfigXML.loadStringXML(text);

			final com.typesafe.config.Config typeConf = ConfigXML
					.applyXML(xdoc);

			return ConfigAny.toString(typeConf);

		} catch (final Exception e) {
			log.debug("invalid xml text", e);
			return "{}";
		}

	}

	public static String toStringXML(final Config config) {

		final String text = config.getXmlConfig();

		if (text == null) {
			log.debug("invalid xml text", new Exception());
			return "<hazelcast/>";
		}

		try {
			final Document xdoc = ConfigXML.loadStringXML(text);
			return ConfigXML.toString(xdoc);
		} catch (final Exception e) {
			log.debug("invalid xml text", e);
			return "<hazelcast/>";
		}

	}

}
