package com.carrotgarden.conf.base.api;

/** instance identity descriptor */
public interface Identity {

	boolean isAvailable();

	String getId();

	IdentitySource getSource();

}
