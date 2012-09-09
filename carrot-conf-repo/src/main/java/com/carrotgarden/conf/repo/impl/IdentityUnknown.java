/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.repo.impl;

import com.carrotgarden.conf.id.api.Identity;

class IdentityUnknown implements Identity {

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public String getId() {
		return "";
	}

	@Override
	public Source getSource() {
		return Source.UNKNONW;
	}

}
