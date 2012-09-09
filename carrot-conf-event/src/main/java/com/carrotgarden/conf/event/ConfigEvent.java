/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.event;

import com.carrotgarden.osgi.event.api.EventUtil;

/** configuration management events */
public interface ConfigEvent {

	/** listen to all config events */
	String ALL = EventUtil.name("*");

	/** fired on successful configuration change */
	String CONFIG_CHANGE = EventUtil.name("CONFIG_CHANGE");

	/** calendar job execution status */
	String JOB_INDENTITY_SUCCESS = EventUtil.name("JOB_INDENTITY_SUCCESS");
	/** calendar job execution status */
	String JOB_INDENTITY_FAILURE = EventUtil.name("JOB_INDENTITY_FAILURE");

	/** calendar job execution status */
	String JOB_VERSION_SUCCESS = EventUtil.name("JOB_VERSION_SUCCESS");
	/** calendar job execution status */
	String JOB_VERSION_FAILURE = EventUtil.name("JOB_VERSION_FAILURE");

	/** calendar job execution status */
	String JOB_RESTART_SUCCESS = EventUtil.name("JOB_RESTART_SUCCESS");
	/** calendar job execution status */
	String JOB_RESTART_FAILURE = EventUtil.name("JOB_RESTART_FAILURE");

}
