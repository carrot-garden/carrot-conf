/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.file;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileLink {

	private final static Logger log = LoggerFactory.getLogger(FileLink.class);

	public static boolean makeLink(final File link, final File file) {

		try {

			final File linkAbs = new File(link.getAbsolutePath());
			final File fileAbs = new File(file.getAbsolutePath());

			final Path newLink = linkAbs.toPath();
			final Path existingFile = fileAbs.toPath();

			Files.createSymbolicLink(newLink, existingFile);

			return true;

		} catch (final Exception e) {

			log.error("", e);

			return false;

		}

	}

	public static boolean ensureLink(final File link, final File file) {

		try {

			if (link == null || file == null) {
				log.error("", new Exception("link == null || file == null"));
				return false;
			}

			if (!file.exists()) {
				log.error("", new Exception("!file.exists()"));
				return false;
			}

			final Path linkPath = link.toPath();

			final Path filePath = file.toPath();

			if (Files.isSymbolicLink(linkPath)) {

				final Path target = Files.readSymbolicLink(linkPath);

				final boolean isSamePath = target.toAbsolutePath().equals(
						filePath.toAbsolutePath());

				if (isSamePath) {
					return true;
				} else {
					return link.delete() && makeLink(link, file);
				}

			} else {

				return deleteFiles(link) && makeLink(link, file);

			}

		} catch (final Exception e) {

			log.error("", e);
			return false;

		}

	}

	public static boolean deleteFiles(final File path) {

		boolean isDeleted = true;

		if (path == null || !path.exists()) {
			return isDeleted;
		}

		if (path.isDirectory()) {
			for (final File entry : path.listFiles()) {
				isDeleted = isDeleted && deleteFiles(entry);
			}
		}

		isDeleted = isDeleted && path.delete();

		return isDeleted;

	}

}
