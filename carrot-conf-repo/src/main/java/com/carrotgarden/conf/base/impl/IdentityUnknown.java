package com.carrotgarden.conf.base.impl;

import com.carrotgarden.conf.id.api.Identity;

class IdentityUnknown implements Identity {

	@Override
	public boolean isAvailable() {
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
