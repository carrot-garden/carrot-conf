/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.util;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigRenderOptions;

/**  */
public class ConfigAny {

	private static final Logger log = LoggerFactory.getLogger(ConfigAny.class);

	private static final ConfigRenderOptions FORMAT = ConfigRenderOptions
			.concise().setFormatted(true);

	public static String toString(final Config config) {

		return toStringJSON(config.root());

	}

	public static String toStringJSON(final ConfigObject object) {

		return object.render(FORMAT);

	}

	static boolean getBoolean(final Config config, final String path,
			final boolean missing) {
		if (config == null || path == null) {
			return missing;
		}
		if (config.hasPath(path)) {
			try {
				return config.getBoolean(path);
			} catch (final Exception e) {
				return missing;
			}
		} else {
			return missing;
		}
	}

	static String getString(final Config config, final String path,
			final String missing) {
		if (config == null || path == null) {
			return missing;
		}
		if (config.hasPath(path)) {
			try {
				return config.getString(path);
			} catch (final Exception e) {
				return missing;
			}
		} else {
			return missing;
		}
	}

	static int getInt(final Config config, final String path, final int missing) {
		if (config == null || path == null) {
			return missing;
		}
		if (config.hasPath(path)) {
			try {
				return config.getInt(path);
			} catch (final Exception e) {
				return missing;
			}
		} else {
			return missing;
		}
	}

	static List<? extends Config> getList(final Config config, final String path) {
		if (config == null || path == null) {
			return Collections.emptyList();
		}
		if (config.hasPath(path)) {
			try {
				return config.getConfigList(path);
			} catch (final Exception e) {
				return Collections.emptyList();
			}
		} else {
			return Collections.emptyList();
		}

	}

}
