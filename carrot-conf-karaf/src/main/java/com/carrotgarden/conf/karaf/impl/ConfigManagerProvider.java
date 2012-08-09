/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.karaf.impl;

import java.io.File;
import java.util.Date;

import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.base.api.ConfigService;
import com.carrotgarden.conf.file.FileLink;
import com.carrotgarden.conf.karaf.api.ConfigManager;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * 
 */
@Component(immediate = true)
public class ConfigManagerProvider implements ConfigManager {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	/** bundle class loader */
	private static ClassLoader loader() {
		return ConfigManagerProvider.class.getClassLoader();
	}

	/** job name convention */
	private static String name(final String name) {
		return ConfigManagerProvider.class.getName() + "/" + name;
	}

	/** unique job names */
	private static final String JOB_ACTIVATE = name("activate");
	private static final String JOB_IDENTITY = name("identity");
	private static final String JOB_VERSION = name("version");
	private static final String JOB_RESTART = name("restart");

	/**
	 * guard against job invocation that can happen after component is
	 * deactivated
	 */
	private boolean isActive() {
		return configService != null && scheduler != null;
	}

	/** runs on component activation */
	private final Runnable jobActivate = new Runnable() {
		@Override
		public void run() {
			log.debug("### activate ###");
			if (isActive()) {
				processActivate();
				processCalendar();
				processLink();
			}
		}
	};

	/** runs on "identity" calendar */
	private final Runnable jobIdentity = new Runnable() {
		@Override
		public void run() {
			log.debug("### identity ###");
			if (isActive()) {
				processIdentity();
			}
		}
	};

	/** runs on "restart" calendar */
	private final Runnable jobRestart = new Runnable() {
		@Override
		public void run() {
			log.debug("### restart ###");
			if (isActive()) {
				processRestart();
			}
		}
	};

	/** runs on "version" calendar */
	private final Runnable jobVersion = new Runnable() {
		@Override
		public void run() {
			log.debug("### version ###");
			if (isActive()) {
				processUpdate();
				processCalendar();
			}
		}
	};

	/** initial configuration download */
	private void processActivate() {

		configService.updateIdentity();
		configService.updateVersion();
		configService.updateMaster();

	}

	/** re-discover instance identity */
	private void processIdentity() {

		configService.updateIdentity();

	}

	/** re-schedule all config jobs */
	private void processCalendar() {

		final String calendarKey = "carrot.config.calendar";

		final Config reference = ConfigFactory.defaultReference(loader());

		final Config config = configService.getVersionConfig()
				.withFallback(reference).getConfig(calendarKey);

		log.debug("calendar : {}", config);

		schedule(config.getConfig("identity"), JOB_IDENTITY, jobIdentity);
		schedule(config.getConfig("restart"), JOB_RESTART, jobRestart);
		schedule(config.getConfig("version"), JOB_VERSION, jobVersion);

	}

	/** ensure config repo and work dir link */
	private void processLink() {

		/** ${karaf.base}/conf/ */
		final File link = new File("./conf");

		/** ${master}/instance/com/example/karaf/ */
		final File conf = configService.getMasterInstance();

		final boolean isLinked = FileLink.ensureLink(link, conf);

		if (isLinked) {
			log.debug("link : {}", link);
			log.debug("conf : {}", conf);
			log.debug("isLinked : {}", isLinked);
		} else {
			log.error("link : {}", link);
			log.error("conf : {}", conf);
			log.error("link failure", new Exception());
		}

	}

	/** update version; if changed - update master */
	private void processUpdate() {

		final String versionKey = "carrot.config.version";

		/** before update */
		final Config one = configService.getVersionConfig();

		configService.updateVersion();

		/** after update */
		final Config two = configService.getVersionConfig();

		final boolean hasOne = one.hasPath(versionKey);
		final boolean hasTwo = two.hasPath(versionKey);

		if (hasOne && hasTwo) {

			final String versionOne = one.getString(versionKey);
			final String versionTwo = two.getString(versionKey);

			/** either version change or we are pulling master */
			final boolean shouldUpdate = !versionOne.equals(versionTwo)
					|| versionTwo.equals("master");

			if (shouldUpdate) {
				log.info("master change : {} -> {}", versionOne, versionTwo);
				configService.updateMaster();
			} else {
				log.debug("no version change");
				return;
			}

		} else {

			log.error("version invalid; {} / {}", one, two);

		}

	}

	/** TODO restart karaf and/or jvm */
	private void processRestart() {
		try {
			// system.reboot();
		} catch (final Exception e) {
			log.error("reboot failure", e);
		}
	}

	/** submit both "instant" and "schedule" jobs */
	private void schedule(//
			final Config config, //
			final String jobName, //
			final Runnable jobTask //
	) {

		final String zone = config.getString("zone");

		/** one time "instant" job */
		{
			final String instant = config.getString("instant");

			log.debug("instant : {}/{}", jobName, instant);

			// final Date instantDate = new Date(System.currentTimeMillis() + 10
			// * 1000);

			final Date currentDate = new Date();
			final Date instantDate = Util.parseISO(instant);

			if (currentDate.before(instantDate)) {
				try {
					final String name = jobName + "/" + instant;
					scheduler.fireJobAt(name, jobTask, null, instantDate);
				} catch (final Exception e) {
					log.error("", e);
				}
			}
		}

		/** cron pattern "schedule" job */
		{
			// final String schedule = "0 0 0 * * ?";//
			// config.getString("schedule");

			final String schedule = config.getString("schedule");

			log.debug("schedule : {}/{}", jobName, schedule);

			try {
				scheduler.addJob(jobName, jobTask, null, schedule, true);
			} catch (final Exception e) {
				log.error("", e);
			}

		}

	}

	@Activate
	protected void activate() {

		log.debug("activate");

		try {
			scheduler.fireJob(jobActivate, null);
		} catch (final Exception e) {
			log.error("", e);
		}

	}

	@Deactivate
	protected void deactivate() {

		log.debug("deactivate");

		scheduler.removeJob(JOB_ACTIVATE);
		scheduler.removeJob(JOB_IDENTITY);
		scheduler.removeJob(JOB_VERSION);
		scheduler.removeJob(JOB_RESTART);

	}

	//

	private ConfigService configService;

	@Reference
	protected void bind(final ConfigService s) {
		configService = s;
	}

	protected void unbind(final ConfigService s) {
		configService = null;
	}

	//

	private Scheduler scheduler;

	@Reference
	protected void bind(final Scheduler s) {
		scheduler = s;
	}

	protected void unbind(final Scheduler s) {
		scheduler = null;
	}

	//

	// private SystemService system;
	// @Reference
	// protected void bind(final SystemService s) {
	// system = s;
	// }
	// protected void unbind(final SystemService s) {
	// system = null;
	// }

}
