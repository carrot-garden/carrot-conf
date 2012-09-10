/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.sync.api;

import com.carrotgarden.conf.id.api.Identity;
import com.typesafe.config.Config;

/**
 * 
 */
public interface ConfigManager {

	/**
	 * @return is current identity valid
	 */
	boolean isIdentityValid();

	/** @return current identity; can be invalid */
	Identity getIdentity();

	/**
	 * @return is current master valid
	 */
	boolean isConfigValid();

	/**
	 * @return current application.conf; can be invalid
	 */
	Config getConfig();

	//

	/** config admin service pid */
	String SERVICE_PID = "com.carrotgarden.conf.sync";

	/** set to "true" to enable test mode */
	String PROP_TEST_ENABLE = "sync.test.enable";

	/** master repo poll period during testing, seconds */
	String PROP_TEST_PERIOD = "sync.test.period";

	/** alternative application.conf file */
	String PROP_TEST_CONFIG = "sync.test.config";

}
