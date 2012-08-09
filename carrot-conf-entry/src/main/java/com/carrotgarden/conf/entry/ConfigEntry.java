/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.entry;

import java.util.HashMap;
import java.util.Map;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValueFactory;

public class ConfigEntry {

	public static final String KEY_ID = "id";

	protected final Map<String, Object> props = new HashMap<String, Object>();

	protected ConfigEntry() {

	}

	protected ConfigEntry(final Map<String, Object> props) {
		props().putAll(props);
	}

	protected ConfigEntry(final Config config) {
		this(config.root().unwrapped());
	}

	public Map<String, Object> props() {
		return props;
	}

	//

	public boolean isValidId() {
		return isValid(getId());
	}

	protected boolean isValid(final CharSequence text) {
		return text != null && text.length() > 0;
	}

	public String getId() {
		return load(KEY_ID);
	}

	public void setId(final String id) {
		save(KEY_ID, id);
	}

	//

	@SuppressWarnings("unchecked")
	public <T> T load(final String key) {
		if (key == null) {
			return null;
		}
		return (T) props.get(key);
	}

	public <T> void save(final String key, final T value) {
		props.put(key, value);
	}

	public boolean hasInt(final String key) {
		if (key == null) {
			return false;
		}
		final Object value = load(key);
		if (value instanceof Number) {
			return true;
		}
		return false;
	}

	public int getInt(final String key) {
		final Object value = load(key);
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		return 0;
	}

	public boolean hasString(final String key) {
		if (key == null) {
			return false;
		}
		final Object value = load(key);
		if (value instanceof String) {
			return true;
		}
		return false;
	}

	public String getString(final String key) {
		if (key == null) {
			return "";
		}
		final Object value = load(key);
		if (value == null) {
			return "";
		}
		return value.toString();
	}

	private static final ConfigRenderOptions FORMAT = ConfigRenderOptions
			.concise().setFormatted(true);

	@Override
	public String toString() {
		return toConfig().root().render(FORMAT);
	}

	public Config toConfig() {
		return ConfigValueFactory.fromMap(props, getId()).toConfig();
	}

}
