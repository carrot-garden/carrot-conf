/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.util;

import java.util.Map;
import java.util.Properties;



import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

/**  */
public class ConfigProps {

	public static Properties propsFrom(final Config conf) {

		final Properties props = new Properties();

		if (conf == null) {
			return props;
		}

		for (final Map.Entry<String, ConfigValue> entry : conf.entrySet()) {
			props.put(entry.getKey(), entry.getValue().unwrapped());
		}

		return props;

	}

}
