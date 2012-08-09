/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.base.impl;

import com.typesafe.config.Config;

public class ConstValues {

	private final Config config;

	public ConstValues(final Config config) {
		this.config = config;
	}

	public String idAmazonUrlEC2() {
		return config.getString("id.amazon-url-ec2");
	}

	public String idEnvironmentVariable() {
		return config.getString("id.environment-variable");
	}

	public String idProgramHomeFile() {
		return config.getString("id.program-home-file");
	}

	public String idSystemProperty() {
		return config.getString("id.system-property");
	}

	public String idUserHomeFile() {
		return config.getString("id.user-home-file");
	}

	public String keyIdentity() {
		return config.getString("key.identity");
	}

	public String keyRepository() {
		return config.getString("key.repository");
	}

	public String keyVersion() {
		return config.getString("key.version");
	}

	public String repoFileApplication() {
		return config.getString("repo.file-application");
	}

	public String repoFileVersion() {
		return config.getString("repo.file-version");
	}

	public String repoFolderInstance() {
		return config.getString("repo.folder-instance");
	}

}
