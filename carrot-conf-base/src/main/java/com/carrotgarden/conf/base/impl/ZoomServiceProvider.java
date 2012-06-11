package com.carrotgarden.conf.base.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.base.api.ZoomService;

@Component(immediate = true)
public class ZoomServiceProvider implements ZoomService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Activate
	protected void activate() {
		log.debug("### actvate");
	}

	@Deactivate
	protected void deactivate() {
		log.debug("### deactvate");
	}

}
