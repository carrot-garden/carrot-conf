/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.util;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

@Ignore
public class TestConfigXML {

	private static final Logger log = LoggerFactory
			.getLogger(TestConfigXML.class);

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	// @Test
	public void testApplyNodeObjectConfig() throws Exception {

		final Config typeConf = ConfigFactory.load("case-01/hazel.conf");

		final Config hazelcast = typeConf.getConfig("hazelcast");

		log.debug("hazelcast : {}", hazelcast);

		final Document document = ConfigXML.builder().newDocument();
		final Element element = document.createElement("hazelcast");
		document.appendChild(element);

		ConfigXML.applyConfig(hazelcast.root(), document, element);

		log.debug("document : {}", ConfigXML.toString(document));

	}

	// @Test
	public void testApplyXML() throws Exception {

		final Document xdoc = ConfigXML
				.loadClassPathXML("reference/hazelcast-fullconfig.xml");

		final Config conf = ConfigXML.applyXML(xdoc);

		log.debug("config : \n{}", conf.root().render(options));

	}

	private final ConfigRenderOptions options = ConfigRenderOptions.concise()
			.setFormatted(true);

	private void assertMatch(final Document source, final Document target)
			throws Exception {

		source.normalizeDocument();
		target.normalizeDocument();

		final String sourceText = ConfigXML.toString(source);

		final String targetText = ConfigXML.toString(target);

		final Document sourceDoc = ConfigXML.loadStringXML(sourceText);
		sourceDoc.normalizeDocument();

		final Document targetDoc = ConfigXML.loadStringXML(targetText);
		targetDoc.normalizeDocument();

		// assertEquals(sourceDoc, targetDoc);

		XMLAssert.assertXMLEqual(sourceDoc, targetDoc);

	}

	@Test
	public void testRoundTrip() throws Exception {

		final Document source = ConfigXML
				.loadClassPathXML("reference/hazelcast-fullconfig.xml");

		final Config config = ConfigXML.applyXML(source);

		final Config hazelcast = config.getConfig("hazelcast");

		final Document target = ConfigXML.builder().newDocument();
		target.appendChild(target.createElement("hazelcast"));

		ConfigXML.applyConfig(hazelcast, target);

		ConfigXML.removeAll(source, Node.COMMENT_NODE, null);
		ConfigXML.removeAll(target, Node.COMMENT_NODE, null);
		ConfigXML.removeAll(source, Node.TEXT_NODE, null);
		ConfigXML.removeAll(target, Node.TEXT_NODE, null);

		log.debug("source : \n{}", ConfigXML.toString(source));

		log.debug("config : \n{}", config.root().render(options));

		log.debug("target : \n{}", ConfigXML.toString(target));

		assertMatch(source, target);

	}

}
