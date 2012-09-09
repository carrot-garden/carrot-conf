/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.id.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.id.api.Constant;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Util {

	private static final Logger log = LoggerFactory.getLogger(Util.class);

	public static Constant constant() {
		return new Constant(reference().getConfig("carrot.config.const"));
	}

	public static String loadUrlAsString(final String urlPath) throws Exception {

		final URL url = new URL(urlPath);

		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				url.openConnection().getInputStream()));

		final StringBuilder text = new StringBuilder(1024);

		while (true) {

			final String line = reader.readLine();

			if (line == null) {
				break;
			}

			text.append(line);

		}

		reader.close();

		return text.toString();

	}

	public static String loadFileAsString(final String file) throws Exception {

		final FileInputStream stream = new FileInputStream(new File(file));

		final InputStreamReader reader = new InputStreamReader(stream, "UTF-8");

		final BufferedReader buffered = new BufferedReader(reader);

		final StringBuilder text = new StringBuilder(1024);

		while (true) {

			final String line = buffered.readLine();

			if (line == null) {
				break;
			}

			text.append(line);
			text.append("\n");

		}

		buffered.close();

		return text.toString();

	}

	public static void saveFileAsString(final String file, final String text)
			throws Exception {

		final PrintWriter writer = new PrintWriter(file, "UTF-8");

		writer.write(text);

		writer.close();

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

	/** convert karaf.domain.com => /instance/com/domain/karaf */
	public static String instancePathFromInstanceId(final String root,
			final String id) {

		if (id == null || id.length() == 0) {
			log.error("invalid id", new Exception());
			return root;
		}

		final String[] array = id.split("\\.");

		final int size = array.length;

		final StringBuilder path = new StringBuilder(128);

		path.append(root);

		for (int k = size - 1; k >= 0; k--) {
			path.append("/");
			path.append(array[k]);
		}

		return path.toString();

	}

	public static String instancePathTrimLast(final String root,
			final String path) {

		if (path == null || path.length() == 0 || !path.startsWith(root)) {
			log.error("invalid path", new Exception());
			return root;
		}

		if (root.equals(path)) {
			return root;
		}

		final int index = path.lastIndexOf("/");

		return path.substring(0, index);

	}

	/** use classloder for osgi case */
	public static Config reference() {

		final ClassLoader loader = Util.class.getClassLoader();

		return ConfigFactory.defaultReference(loader);

	}

	/** iterate upwards file system */
	public static File findFolderWithSuffix(final File folder,
			final String suffix) {

		if (folder == null || suffix == null) {
			return null;
		}

		if (folder.getAbsolutePath().endsWith(suffix)) {
			return folder;
		}

		return findFolderWithSuffix(folder.getParentFile(), suffix);

	}

}
