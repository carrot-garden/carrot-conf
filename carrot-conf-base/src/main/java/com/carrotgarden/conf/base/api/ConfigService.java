/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.base.api;

import java.io.File;

import com.typesafe.config.Config;

public interface ConfigService {

	boolean isConfigAvailable();

	void updateConfig();

	Config getInstanceConfig();

	File getConfigFolder();

}
