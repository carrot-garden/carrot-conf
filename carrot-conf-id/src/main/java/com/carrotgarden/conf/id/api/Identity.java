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
		PARENT_FOLDER_NAME, //
		PROGRAM_HOME_FILE(), //
		USER_HOME_FILE(), //
		AMAZON_URL_EC2(), //
		UNKNONW(), // default
	}

	boolean isValid();

	String getId();

	Source getSource();

	/** based on {@link #getId()} */
	@Override
	boolean equals(final Object other);

	/** {@link #getId()} for invalid identity */
	String INVALID_ID = "";

}
