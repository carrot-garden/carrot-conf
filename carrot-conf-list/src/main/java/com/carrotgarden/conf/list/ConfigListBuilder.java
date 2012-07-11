/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.list;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import com.typesafe.config.ConfigValueType;

/** list builder service */
public class ConfigListBuilder {

	private static final Logger log = LoggerFactory
			.getLogger(ConfigListBuilder.class);

	/** suffix after the list name that will be used to discover a list builder */
	public static final String BUILDER_SUFFIX = "/builder";

	/** recursively process all lists in #Config */
	public static Config process(final Config conf) {

		return process(conf.root()).toConfig();

	}

	/** recursively process all lists in #ConfigObject */
	public static ConfigObject process(final ConfigObject confRoot) {

		ConfigObject result = confRoot;

		for (final Map.Entry<String, ConfigValue> entry : confRoot.entrySet()) {

			final String key = entry.getKey();
			final ConfigValue value = entry.getValue();

			if (value == null) {
				continue;
			}

			final ConfigValueType type = value.valueType();

			switch (type) {
			case NULL:
			case BOOLEAN:
			case NUMBER:
			case STRING:
				//
				continue;

			case LIST:

				final ConfigValue builder = result.get(key + BUILDER_SUFFIX);

				if (builder == null) {
					// list builder not provided
					continue;
				}

				if (builder.valueType() != ConfigValueType.OBJECT) {
					log.error("list bulder must be an object", new Exception(
							builder.toString()));
					continue;
				}

				final ConfigList sourceList = ((ConfigList) value);

				final List<Object> builderList = new LinkedList<Object>();

				for (final ConfigValue sourceItem : sourceList) {

					final Object builderItem = sourceItem.withFallback(builder)
							.unwrapped();

					builderList.add(builderItem);

				}

				final ConfigList targetList = ConfigValueFactory
						.fromIterable(builderList);

				result = result.withValue(key, targetList);

				continue;

			case OBJECT:
				result = result.withValue(key, process((ConfigObject) value));
				continue;

			default:
				log.error("unsupported type", new Exception(type.name()));
				continue;

			}

		}

		return result;

	}

}
