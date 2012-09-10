/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.file;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileWatch {

	private final static Logger log = LoggerFactory.getLogger(FileWatch.class);

	private static final WatchService watcher;

	static {

		WatchService watcherTry = null;

		try {
			watcherTry = FileSystems.getDefault().newWatchService();
		} catch (final Exception e) {
			log.error("", e);
		}

		watcher = watcherTry;

	}

	private static String key(final File folder) {
		return folder.getAbsolutePath();
	}

	private static Path path(final File folder) {
		return new File(folder.getAbsolutePath()).toPath();
	}

	private static final Map<String, WatchKey> watchMap = new HashMap<String, WatchKey>();

	public static synchronized boolean pathAdd(final File folder) {

		try {

			if (watchMap.containsKey(key(folder))) {
				return true;
			}

			final Path path = path(folder);

			final WatchKey watch = path.register(watcher, ENTRY_CREATE,
					ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW);

			watch.reset();

			watchMap.put(key(folder), watch);

			return true;

		} catch (final Exception e) {

			log.error("", e);

			return false;

		}

	}

	public static synchronized boolean pathRemove(final File folder) {

		try {

			if (!watchMap.containsKey(key(folder))) {
				return true;
			}

			final WatchKey watch = watchMap.remove(key(folder));

			watch.cancel();

			return true;

		} catch (final Exception e) {

			log.error("", e);

			return false;

		}

	}

	public static synchronized List<File> pathCheck(final File folder) {

		final List<File> fileList = new LinkedList<File>();

		try {

			if (!watchMap.containsKey(key(folder))) {
				log.warn("registration missing", new Exception());
				return fileList;
			}

			final WatchKey watch = watchMap.get(key(folder));

			final List<WatchEvent<?>> eventList = watch.pollEvents();

			watch.reset();

			// log.debug("eventList : {}", eventList);

			for (final WatchEvent<?> event : eventList) {

				final Kind<?> kind = event.kind();

				if (kind == OVERFLOW) {
					// continue;
				}

				final Path path = (Path) event.context();

				fileList.add(path.toFile());

			}

		} catch (final Exception e) {

			log.error("", e);

		}

		return fileList;

	}

}
