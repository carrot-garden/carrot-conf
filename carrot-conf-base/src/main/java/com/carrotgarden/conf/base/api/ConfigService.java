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

	/** current identity */
	Identity getIdentity();

	//

	/** current application.conf */
	Config getMasterConfig();

	/** master repo root */
	File getMasterRoot();

	/** master instance folder, location of application.conf */
	File getMasterInstance();

	//

	/** current version.conf */
	Config getVersionConfig();

	/** version repo root */
	File getVersionRoot();

	/** version instance folder, location of version.conf */
	File getVersionInstance();

	//

	boolean isIdentityValid();

	boolean isMasterValid();

	boolean isVersionValid();

	//

	/** discover identity */
	void updateIdentity();

	/** does not fetch archon; update master from archon */
	void updateMaster();

	/** fetch archon from remote, update version from archon */
	void updateVersion();

}
