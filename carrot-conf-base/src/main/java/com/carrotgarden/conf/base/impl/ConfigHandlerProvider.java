/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.base.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Property;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.url.URLConstants;
import org.osgi.service.url.URLStreamHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.base.api.ConfigService;

/** custom protocol handler to access configuration repository */
@Component(immediate = true)
public class ConfigHandlerProvider extends URLHandlerBase implements
		URLStreamHandlerService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Property(name = URLConstants.URL_HANDLER_PROTOCOL)
	public static final String PROTOCOL = "config";

	@Override
	public URLConnection openConnection(final URL url) throws IOException {

		log.debug("url : {}", url);

		final File repo = configService.getMasterRoot();

		final File file = new File(repo, url.getPath());

		final ConfigConnection connection = new ConfigConnection(file);

		return connection;

	}

	private ConfigService configService;

	@Reference
	protected void bind(final ConfigService s) {
		configService = s;
	}

	protected void unbind(final ConfigService s) {
		configService = null;
	}

	@Activate
	protected void activate() {
		log.debug("activate");
	}

	@Deactivate
	protected void deactivate() {
		log.debug("deactivate");
	}

}
