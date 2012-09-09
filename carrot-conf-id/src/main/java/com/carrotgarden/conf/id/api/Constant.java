/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.id.api;

import com.typesafe.config.Config;

/** see reference.conf */
public class Constant {

	private final Config config;

	public Constant(final Config config) {
		this.config = config;
	}

	public String activeIdentity() {
		return config.getString("active.identity");
	}

	public String idAmazonUrlEC2() {
		return config.getString("id.amazon-url-ec2");
	}

	public String idEnvironmentVariable() {
		return config.getString("id.environment-variable");
	}

	public String idFolderDomainSuffix() {
		return config.getString("id.folder-domain-suffix");
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
