/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.touch.api;

/**
 * allows to update arbitrary osgi component properties after successful git
 * repo configuration update
 */
public interface ConfigTouch {

	/**
	 * factory persistent id of this service; use it in the name of *.cfg files
	 * as follows:
	 * 
	 * ${karaf.base}/etc/com.carrotgarden.conf.touch-xxx.cfg
	 * 
	 * where xxx is child instance id
	 * 
	 */
	String SERVICE_FACTORY_PID = "com.carrotgarden.conf.touch";

	//

	/** set to "true" to update config file time stamp */
	String KEY_FILE_TOUCH = "component.file.touch";

	/** configuration file to update time stamp on */
	String KEY_FILE_NAME = "component.file.name";

	//

	/** set to "true" to update config from properties */
	String KEY_SERVICE_TOUCH = "component.service.touch";

	/** PID of a component to be updated */
	String KEY_SERVICE_PID = "component.service.pid";

	/** "\n" separated properties entries to update */
	String KEY_SERVICE_PROPS = "component.service.properties";

}
