/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;

/**  */
public class ConfigXML {

	private static final Logger log = LoggerFactory.getLogger(ConfigXML.class);

	/** root/attribute prefix */
	public static final String PRE = "/";

	private static DocumentBuilder builder;

	static DocumentBuilder builder() throws Exception {

		if (builder == null) {

			final DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();

			builder = factory.newDocumentBuilder();

		}

		return builder;

	}

	public static String toString(final Document document) throws Exception {

		final TransformerFactory factory = TransformerFactory.newInstance();

		factory.setAttribute("indent-number", 3);

		final Transformer transformer = factory.newTransformer();

		transformer.setOutputProperty("indent", "yes");

		final StreamResult result = new StreamResult(new StringWriter());

		final DOMSource source = new DOMSource(document);

		transformer.transform(source, result);

		final String xml = result.getWriter().toString();

		return xml;

	}

	/** produce new empty #ConfigObject */
	public static ConfigObject emptyObject() {
		return ConfigFactory.empty().root();
	}

	private static boolean hasPrintable(final Node node) {
		return node.getTextContent().matches("\\S+");
	}

	private static boolean isNodePrimitive(final Node root) {

		int count = 0;

		final NodeList list = root.getChildNodes();

		for (int index = 0; index < list.getLength(); index++) {

			final Node node = list.item(index);

			if (node.getNodeType() == Node.TEXT_NODE) {
				count++;
			}

		}

		return count == list.getLength();

	}

	private static boolean isNodeObject(final Node root) {
		if (root.hasAttributes()) {
			return true;
		}
		if (!isNodePrimitive(root)) {
			return true;
		}
		return false;
	}

	private static boolean isNodeList(final Node root) {

		final Set<String> set = new HashSet<String>();

		final NodeList list = root.getChildNodes();

		for (int index = 0; index < list.getLength(); index++) {

			final Node node = list.item(index);

			if (node.getNodeType() == Node.ELEMENT_NODE) {

				final Element element = (Element) node;

				final String name = element.getTagName();

				if (set.contains(name)) {
					return true;
				}

				set.add(name);

			}

		}

		return false;

	}

	private static ConfigObject applyNode(ConfigObject result, final Node node) {

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			if (isNodeList(node)) {
				result = applyNodeList(result, node);
			} else if (isNodeObject(node)) {
				result = applyNodeObject(result, node);
			} else if (isNodePrimitive(node)) {
				result = applyNodePrimitive(result, node);
			}
		}

		return result;

	}

	/** generate ConfigObject from XML */
	public static Config applyXML(final Document xdoc) {

		final Element root = xdoc.getDocumentElement();

		final String key = root.getNodeName();
		final ConfigValue value = applyXML(root);

		return emptyObject().withValue(key, value).toConfig();

	}

	/** generate ConfigObject from XML */
	public static ConfigObject applyXML(final Node root) {

		ConfigObject result = emptyObject();

		result = applyNodeAttributes(result, root);

		final NodeList list = root.getChildNodes();

		for (int index = 0; index < list.getLength(); index++) {
			result = applyNode(result, list.item(index));
		}

		return result;

	}

	private static ConfigObject applyNodeAttributes(ConfigObject result,
			final Node root) {

		if (root.hasAttributes()) {

			final NamedNodeMap attrMap = root.getAttributes();

			for (int index = 0; index < attrMap.getLength(); index++) {

				final Node node = attrMap.item(index);

				final String key = PRE + node.getNodeName();

				final ConfigValue value = //
				ConfigValueFactory.fromAnyRef(node.getNodeValue());

				result = result.withValue(key, value);

			}

			if (isNodePrimitive(root) && hasPrintable(root)) {

				final String key = PRE;

				final ConfigValue value = //
				ConfigValueFactory.fromAnyRef(root.getTextContent());

				result = result.withValue(key, value);

			}

		}

		return result;

	}

	private static ConfigObject applyNodePrimitive(ConfigObject result,
			final Node node) {

		final String key = node.getNodeName();
		final ConfigValue value = ConfigValueFactory.fromAnyRef(node
				.getTextContent());

		result = result.withValue(key, value);

		return result;

	}

	private static ConfigObject applyNodeList(ConfigObject result,
			final Node root) {

		final NodeList nodeList = root.getChildNodes();

		final List<Object> entryList = new LinkedList<Object>();

		for (int index = 0; index < nodeList.getLength(); index++) {

			final Node node = nodeList.item(index);

			if (node.getNodeType() == Node.ELEMENT_NODE) {

				final ConfigObject object = applyNode(emptyObject(), node);

				entryList.add(object.unwrapped());

			}

		}

		final ConfigList configList = ConfigValueFactory
				.fromIterable(entryList);

		result = result.withValue(root.getNodeName(), configList);

		return result;

	}

	private static ConfigObject applyNodeObject(ConfigObject result,
			final Node node) {

		final String key = node.getNodeName();
		final ConfigValue value = applyXML(node);

		result = result.withValue(key, value);

		return result;

	}

	/** generate XML from ConfigObject */
	public static void applyConfig(final Config config, final Document document) {

		applyConfig(config.root(), document, document.getDocumentElement());

	}

	/** generate XML from ConfigObject */
	public static void applyConfig(final ConfigObject object,
			final Document document, final Element root) {

		for (final Entry<String, ConfigValue> entry : object.entrySet()) {

			final String key = entry.getKey();
			final ConfigValue value = entry.getValue();

			final String text = value.unwrapped().toString();

			final boolean isRoot = PRE.equals(key);
			final boolean isNode = !key.startsWith(PRE);
			final boolean isAttr = !isRoot && !isNode;

			if (isAttr) {

				/** attribute value */
				root.setAttribute(key.substring(1), text);

			} else {

				/** element value */

				final Element node;
				if (isRoot) {
					/** self reference */
					node = root;
				} else {
					node = document.createElement(key);
					root.appendChild(node);
				}

				switch (value.valueType()) {

				default:
					node.setTextContent(text);
					break;

				case OBJECT:
					applyConfig((ConfigObject) value, document, node);
					break;

				case LIST:
					final ConfigList configList = (ConfigList) value;
					for (final ConfigValue item : configList) {
						applyConfig((ConfigObject) item, document, node);
					}
					break;

				}

			}

		}

	}

	public static Document loadStringXML(final String text) throws Exception {

		final byte[] array = text.getBytes(Charset.forName("UTF-8"));

		final InputStream input = new ByteArrayInputStream(array);

		final Document document = builder().parse(input);

		return document;

	}

	public static Document loadClassPathXML(final String file) throws Exception {

		final InputStream input = ConfigProps.class.getClassLoader()
				.getResourceAsStream(file);

		final Document document = builder().parse(input);

		return document;

	}

	public static void removeAll(final Node node, final int nodeType,
			final String name) {

		if (node.getNodeType() == nodeType
				&& (name == null || node.getNodeName().equals(name))) {
			node.getParentNode().removeChild(node);

		} else {
			final NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				removeAll(list.item(i), nodeType, name);
			}
		}

	}

}
