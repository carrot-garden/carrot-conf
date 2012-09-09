/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.repo.impl;

import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.osgi.service.url.URLStreamHandlerService;
import org.osgi.service.url.URLStreamHandlerSetter;

/**
 * Abstract implementation of the {@code URLStreamHandlerService} interface. All
 * the methods simply invoke the corresponding methods on
 * {@code java.net.URLStreamHandler} except for {@code parseURL} and
 * {@code setURL}, which use the {@code URLStreamHandlerSetter} parameter.
 * Subclasses of this abstract class should not need to override the
 * {@code setURL} and {@code parseURL(URLStreamHandlerSetter,...)} methods.
 * 
 * @ThreadSafe
 * @version $Id: 465a0ed86f5d49b338ffc6a13bb68f60f04e54d6 $
 */

/* difference from osgi-core: custom setURL() for non-osgi testing */

abstract class URLHandlerBase extends URLStreamHandler implements
		URLStreamHandlerService {
	/**
	 * @see "java.net.URLStreamHandler.openConnection"
	 */
	@Override
	public abstract URLConnection openConnection(URL u)
			throws java.io.IOException;

	/**
	 * The {@code URLStreamHandlerSetter} object passed to the parseURL method.
	 */
	protected volatile URLStreamHandlerSetter realHandler;

	/**
	 * Parse a URL using the {@code URLStreamHandlerSetter} object. This method
	 * sets the {@code realHandler} field with the specified
	 * {@code URLStreamHandlerSetter} object and then calls
	 * {@code parseURL(URL,String,int,int)}.
	 * 
	 * @param realHandler
	 *            The object on which the {@code setURL} method must be invoked
	 *            for the specified URL.
	 * @see "java.net.URLStreamHandler.parseURL"
	 */
	@Override
	public void parseURL(final URLStreamHandlerSetter realHandler, final URL u,
			final String spec, final int start, final int limit) {
		this.realHandler = realHandler;
		parseURL(u, spec, start, limit);
	}

	/**
	 * This method calls {@code super.toExternalForm}.
	 * 
	 * @see "java.net.URLStreamHandler.toExternalForm"
	 */
	@Override
	public String toExternalForm(final URL u) {
		return super.toExternalForm(u);
	}

	/**
	 * This method calls {@code super.equals(URL,URL)}.
	 * 
	 * @see "java.net.URLStreamHandler.equals(URL,URL)"
	 */
	@Override
	public boolean equals(final URL u1, final URL u2) {
		return super.equals(u1, u2);
	}

	/**
	 * This method calls {@code super.getDefaultPort}.
	 * 
	 * @see "java.net.URLStreamHandler.getDefaultPort"
	 */
	@Override
	public int getDefaultPort() {
		return super.getDefaultPort();
	}

	/**
	 * This method calls {@code super.getHostAddress}.
	 * 
	 * @see "java.net.URLStreamHandler.getHostAddress"
	 */
	@Override
	public InetAddress getHostAddress(final URL u) {
		return super.getHostAddress(u);
	}

	/**
	 * This method calls {@code super.hashCode(URL)}.
	 * 
	 * @see "java.net.URLStreamHandler.hashCode(URL)"
	 */
	@Override
	public int hashCode(final URL u) {
		return super.hashCode(u);
	}

	/**
	 * This method calls {@code super.hostsEqual}.
	 * 
	 * @see "java.net.URLStreamHandler.hostsEqual"
	 */
	@Override
	public boolean hostsEqual(final URL u1, final URL u2) {
		return super.hostsEqual(u1, u2);
	}

	/**
	 * This method calls {@code super.sameFile}.
	 * 
	 * @see "java.net.URLStreamHandler.sameFile"
	 */
	@Override
	public boolean sameFile(final URL u1, final URL u2) {
		return super.sameFile(u1, u2);
	}

	/**
	 * This method calls
	 * {@code realHandler.setURL(URL,String,String,int,String,String)}.
	 * 
	 * @see "java.net.URLStreamHandler.setURL(URL,String,String,int,String,String)"
	 * @deprecated This method is only for compatibility with handlers written
	 *             for JDK 1.1.
	 */
	@Deprecated
	@Override
	protected void setURL(final URL u, final String proto, final String host,
			final int port, final String file, final String ref) {

		if (realHandler == null) {
			// non-osgi mode
			super.setURL(u, proto, host, port, file, ref);
		} else {
			realHandler.setURL(u, proto, host, port, file, ref);
		}

	}

	/**
	 * This method calls
	 * {@code realHandler.setURL(URL,String,String,int,String,String,String,String)}
	 * .
	 * 
	 * @see "java.net.URLStreamHandler.setURL(URL,String,String,int,String,String,String,String)"
	 */
	@Override
	protected void setURL(final URL u, final String proto, final String host,
			final int port, final String auth, final String user,
			final String path, final String query, final String ref) {

		if (realHandler == null) {
			// non-osgi mode
			super.setURL(u, proto, host, port, auth, user, path, query, ref);
		} else {
			realHandler.setURL(u, proto, host, port, auth, user, path, query,
					ref);
		}

	}

}
