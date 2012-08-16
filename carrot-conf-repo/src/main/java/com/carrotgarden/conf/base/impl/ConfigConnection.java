/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.base.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

/** file based url connection */
public class ConfigConnection extends URLConnection {

	private final File file;

	public ConfigConnection(final File file) throws IOException {
		super(file.toURI().toURL());
		this.file = file;
	}

	@Override
	public void connect() throws IOException {
		// unused
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

}
