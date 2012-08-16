package com.carrotgarden.conf.karaf;

import org.ops4j.pax.logging.PaxLoggingService;
import org.ops4j.pax.logging.spi.PaxAppender;
import org.ops4j.pax.logging.spi.PaxLoggingEvent;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Property;

@Component(immediate = true)
public class TestAppender implements PaxAppender {

	@Property(name = PaxLoggingService.APPENDER_NAME_PROPERTY)
	public static final String APPENDER_NAME = "TEST";

	@Override
	public void doAppend(final PaxLoggingEvent event) {
		System.err.println("" + event);
	}

}
