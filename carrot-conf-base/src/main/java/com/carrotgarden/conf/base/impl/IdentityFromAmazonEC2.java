package com.carrotgarden.conf.base.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.carrotgarden.conf.base.api.ConfigConst;
import com.carrotgarden.conf.base.api.IdentitySource;
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

	@Override
	protected String getValue() {

		try {

			final URL url = new URL(ConfigConst.Id.AMAZON_EC2_URL);

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

			return conf.getString(ConfigConst.Id.SYSTEM_PROPERTY);

		} catch (final Throwable e) {

			log.debug("bad conf url", e);

			return null;

		}

	}

	@Override
	public IdentitySource getSource() {
		return IdentitySource.AMAZON_EC2;
	}

}
