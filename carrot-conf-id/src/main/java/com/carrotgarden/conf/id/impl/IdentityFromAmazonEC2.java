/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.id.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.carrotgarden.conf.id.api.Constant;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * resolve from
 * 
 * <a href=
 * "http://docs.amazonwebservices.com/AWSEC2/latest/UserGuide/AESDG-chapter-instancedata.html"
 * >user-data</a>
 * */
public class IdentityFromAmazonEC2 extends IdentityFromUnknown {

	protected IdentityFromAmazonEC2(final Constant constValues) {
		super(constValues);
	}

	@Override
	protected String getValue() {

		try {

			final URL url = new URL(constValues.idAmazonUrlEC2());

			final HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			connection.setConnectTimeout(2 * 1000);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "config-reader");
			connection.connect();

			final InputStream input = connection.getInputStream();
			final InputStreamReader reader = new InputStreamReader(input);
			final BufferedReader buffered = new BufferedReader(reader);

			final Config conf = ConfigFactory.parseReader(buffered);

			buffered.close();

			return conf.getString(constValues.keyIdentity());

		} catch (final Throwable e) {

			log.debug("amazon url lookup failure", e);

			return null;

		}

	}

	@Override
	public Source getSource() {
		return Source.AMAZON_URL_EC2;
	}

}
