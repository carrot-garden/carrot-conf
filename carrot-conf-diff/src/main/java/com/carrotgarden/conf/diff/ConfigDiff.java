/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.diff;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import com.typesafe.config.ConfigValueType;

/** compare configurations */
public class ConfigDiff {

	private static final Logger log = LoggerFactory.getLogger(ConfigDiff.class);

	/** compare #Config configurations */
	public static DiffResultConfig compare(//
			final Config confOLD, //
			final Config confNEW //
	) {

		final DiffResultObject result = compare(confOLD.root(), confNEW.root());

		return new DiffResultConfig( //
				result.getDelete().toConfig(), //
				result.getInsert().toConfig(), //
				result.getUpdateFrom().toConfig(), //
				result.getUpdateInto().toConfig(), //
				result.getUpdateNone().toConfig() //
		);

	}

	/** compare #ConfigObject configurations */
	public static DiffResultObject compare(//
			final ConfigObject rootOLD, //
			final ConfigObject rootNEW //
	) {

		ConfigObject delete = emptyObject();
		ConfigObject insert = emptyObject();
		ConfigObject updateFrom = emptyObject();
		ConfigObject updateInto = emptyObject();
		ConfigObject updateNone = emptyObject();

		final Set<String> everyKey = new HashSet<String>();

		everyKey.addAll(rootOLD.keySet());
		everyKey.addAll(rootNEW.keySet());

		for (final String key : everyKey) {

			final ConfigValue valueOLD = rootOLD.get(key);
			final ConfigValue valueNEW = rootNEW.get(key);

			final boolean isNone = (valueOLD == null && valueNEW == null);
			final boolean isDelete = (valueOLD != null && valueNEW == null);
			final boolean isInsert = (valueOLD == null && valueNEW != null);
			final boolean isUpdate = (valueOLD != null && valueNEW != null);

			if (isNone) {
				updateNone = updateNone.withValue(key, valueOLD);
				continue;
			}

			if (isDelete) {
				delete = delete.withValue(key, valueOLD);
				continue;
			}

			if (isInsert) {
				insert = insert.withValue(key, valueNEW);
				continue;
			}

			if (isUpdate) {

				if (valueOLD.valueType() != valueNEW.valueType()) {
					log.warn("ignoring incompatible type at key",
							new Exception(key));
					continue;
				}

				final ConfigValueType type = valueOLD.valueType();

				switch (type) {
				case NULL:
				case BOOLEAN:
				case NUMBER:
				case STRING:
					if (valueOLD.equals(valueNEW)) {
						updateNone = updateNone.withValue(key, valueOLD);
					} else {
						updateFrom = updateFrom.withValue(key, valueOLD);
						updateInto = updateInto.withValue(key, valueNEW);
					}

					continue;

				case LIST:

					final ConfigList listOLD = (ConfigList) valueOLD;
					final ConfigList listNEW = (ConfigList) valueNEW;

					final List<Object> rawListOLD = listOLD.unwrapped();
					final List<Object> rawListNEW = listNEW.unwrapped();

					final List<Object> rawListDelete = new LinkedList<Object>();
					final List<Object> rawListInsert = new LinkedList<Object>();
					final List<Object> rawListUpdateNone = new LinkedList<Object>();

					rawListDelete.addAll(rawListOLD);
					rawListDelete.removeAll(rawListNEW);

					rawListInsert.addAll(rawListNEW);
					rawListInsert.removeAll(rawListOLD);

					rawListUpdateNone.addAll(rawListOLD);
					rawListUpdateNone.addAll(rawListNEW);
					rawListUpdateNone.removeAll(rawListDelete);
					rawListUpdateNone.removeAll(rawListInsert);

					final boolean hasDelete = !rawListDelete.isEmpty();
					final boolean hasInsert = !rawListInsert.isEmpty();
					final boolean hasUpdateNone = !rawListUpdateNone.isEmpty();

					if (hasDelete) {
						final ConfigList listDIFF = ConfigValueFactory
								.fromIterable(rawListDelete);
						delete = delete.withValue(key, listDIFF);
					}

					if (hasInsert) {
						final ConfigList listDIFF = ConfigValueFactory
								.fromIterable(rawListInsert);
						insert = insert.withValue(key, listDIFF);
					}

					if (hasUpdateNone) {
						final ConfigList listDIFF = ConfigValueFactory
								.fromIterable(rawListUpdateNone);
						updateNone = insert.withValue(key, listDIFF);
					}

					continue;

				case OBJECT:

					final ConfigObject objectOLD = (ConfigObject) valueOLD;
					final ConfigObject objectNEW = (ConfigObject) valueNEW;

					final DiffResultObject result = //
					compare(objectOLD, objectNEW);

					if (result.hasDelete()) {
						delete = delete.withValue(key, result.getDelete());
					}

					if (result.hasInsert()) {
						insert = insert.withValue(key, result.getInsert());
					}

					if (result.hasUpdateDiff()) {
						updateFrom = updateFrom.withValue(key,
								result.getUpdateFrom());
						updateInto = updateInto.withValue(key,
								result.getUpdateInto());
					}

					if (result.hasUpdateNone()) {
						updateNone = updateNone.withValue(key,
								result.getUpdateNone());
					}

					continue;

				default:
					log.error("unsupported type", new Exception(type.name()));
					continue;

				}

			}

		}

		return new DiffResultObject(//
				delete, //
				insert, //
				updateFrom, //
				updateInto, //
				updateNone //
		);

	}

	/** produce new empty #Config */
	public static Config emptyConfig() {
		return ConfigFactory.empty();
	}

	/** produce new empty #ConfigObject */
	public static ConfigObject emptyObject() {
		return ConfigFactory.empty().root();
	}

}
