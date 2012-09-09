/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.repo.api;

import java.io.File;

import com.carrotgarden.conf.id.api.Identity;
import com.typesafe.config.Config;

/** low level repository operations */
public interface ConfigService {

	/** current identity; can be invalid */
	Identity getIdentity();

	//

	/** current application.conf; can be invalid */
	Config getMasterConfig();

	/** master repo root; null for invalid */
	File getMasterRoot();

	/** master instance folder, location of application.conf */
	File getMasterInstance();

	//

	/** current version.conf; can be invalid */
	Config getVersionConfig();

	/** version repo root; null for invalid */
	File getVersionRoot();

	/** version instance folder, location of version.conf */
	File getVersionInstance();

	//

	/**
	 * @return is current identity valid
	 */
	boolean isIdentityValid();

	/**
	 * @return is current master valid
	 */
	boolean isMasterValid();

	/**
	 * @return is current version valid
	 */
	boolean isVersionValid();

	//

	/**
	 * discover identity; will replace current identity only on success
	 * 
	 * @return was update a success
	 */
	boolean updateIdentity();

	/**
	 * does not fetch archon from remote; update master from current archon;
	 * will replace current config only on success
	 * 
	 * @return was update a success
	 */
	boolean updateMaster();

	/**
	 * fetch archon from remote, update version from updated archon; will
	 * replace current config only on success
	 * 
	 * @return was update a success
	 */
	boolean updateVersion();

}
