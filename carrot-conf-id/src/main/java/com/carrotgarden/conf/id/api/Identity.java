/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.id.api;

/** instance identity descriptor */
public interface Identity {

	/** ordinal reflects discovery priority */
	enum Source {
		ENVIRONMENT_VARIABLE(), //
		SYSTEM_PROPERTY(), //
		PROGRAM_HOME(), //
		USER_HOME(), //
		AMAZON_URL_EC2(), //
		UNKNONW(), // default

	}

	boolean isAvailable();

	String getId();

	Source getSource();

}
