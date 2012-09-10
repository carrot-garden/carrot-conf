/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.sync.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Util {

	/**
	 * form "2012-01-15T12:15:30Z"
	 */
	public static Date parseISO(final String text) {

		try {

			final Calendar calendar = javax.xml.bind.DatatypeConverter
					.parseDateTime(text);

			return calendar.getTime();

		} catch (final Exception e) {

			return new Date(0);

		}

	}

	public static <T> T[] concat(final T[] one, final T[] two) {

		final T[] result = Arrays.copyOf(one, one.length + two.length);

		System.arraycopy(two, 0, result, one.length, two.length);

		return result;

	}

	/** bundle class loader */
	public static ClassLoader loader() {
		return Util.class.getClassLoader();
	}

	public static Config reference() {
		return ConfigFactory.defaultReference(loader());
	}

	@SuppressWarnings("rawtypes")
	public static Map<String, String> wrap(final Dictionary dict) {

		final Map<String, String> props = new HashMap<String, String>();

		if (dict == null || dict.isEmpty()) {
			return props;
		}

		final Enumeration<?> keyList = dict.keys();

		while (keyList.hasMoreElements()) {

			final Object key = keyList.nextElement();
			final Object value = dict.get(key);

			if (key instanceof String && value instanceof String) {
				props.put(key.toString(), value.toString());
			}

		}

		return props;

	}

	public static long parseLong(final String text, final long defaultValue) {

		try {

			return Long.parseLong(text);

		} catch (final Exception e) {

			return defaultValue;

		}

	}

}
