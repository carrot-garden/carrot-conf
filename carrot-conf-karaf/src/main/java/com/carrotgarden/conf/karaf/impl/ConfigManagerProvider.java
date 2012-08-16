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

	private final static Logger log = LoggerFactory
			.getLogger(ConfigManagerProvider.class);

	private final static String PATH_CALENDAR = "carrot.config.calendar";
	private final static String PATH_VERSION = "carrot.config.version";

	private final static String KEY_IDENTITY = "identity";
	private final static String KEY_RESTART = "restart";
	private final static String KEY_VERSION = "version";

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
	private static final String JOB_IDENTITY = name(KEY_IDENTITY);
	private static final String JOB_RESTART = name(KEY_RESTART);
	private static final String JOB_VERSION = name(KEY_VERSION);

	/**
	 * guard against job invocation that can happen after component is
	 * deactivated
	 */
	private boolean isActive() {
		return configService != null && scheduler != null;
	}

	/** runs once on component activation */
	private final Runnable jobActivate = new Runnable() {
		@Override
		public void run() {
			log.debug("activate");
			final boolean is = isActive() //
					&& processCalendar(true) // default schedule
					&& processActivate() //
					&& processCalendar(false) // instance schedule
			;
		}
	};

	/** runs on "identity" calendar */
	private final Runnable jobIdentity = new Runnable() {
		@Override
		public void run() {
			log.debug("identity");
			final boolean is = isActive() //
					&& processIdentity();
		}
	};

	/** runs on "restart" calendar */
	private final Runnable jobRestart = new Runnable() {
		@Override
		public void run() {
			log.debug("restart");
			final boolean is = isActive() //
					&& processRestart();
		}
	};

	/** runs on "version" calendar */
	private final Runnable jobVersion = new Runnable() {
		@Override
		public void run() {
			log.debug("version");
			final boolean is = isActive() //
					&& processUpdate() //
					&& processLink() //
					&& processCalendar(false);
		}
	};

	/** initial configuration download, if available */
	private boolean processActivate() {

		return true //
				&& configService.updateIdentity() //
				&& configService.updateVersion() //
				&& configService.updateMaster();

	}

	/** re-discover instance identity */
	private boolean processIdentity() {

		return configService.updateIdentity();

	}

	/** re-schedule all config jobs */
	private boolean processCalendar(final boolean isDefault) {

		final Config config;

		final Config reference = ConfigFactory.defaultReference(loader());

		if (isDefault) {

			/** use default reference calendar */

			config = reference.getConfig(PATH_CALENDAR);

		} else {

			/** use provided instance calendar */

			if (!configService.isVersionValid()) {
				log.error("", new IllegalStateException(
						"version must be valid to schedule instance calendar"));
				return false;
			}

			config = configService.getVersionConfig().withFallback(reference)
					.getConfig(PATH_CALENDAR);

		}

		log.debug("calendar : {}", config);

		schedule(config.getConfig(KEY_IDENTITY), JOB_IDENTITY, jobIdentity);
		schedule(config.getConfig(KEY_RESTART), JOB_RESTART, jobRestart);
		schedule(config.getConfig(KEY_VERSION), JOB_VERSION, jobVersion);

		return true;

	}

	/** ensure config repo and work dir link */
	private boolean processLink() {

		if (!configService.isMasterValid()) {
			log.error("", new IllegalStateException(
					"master must be valid in order to process link"));
			return false;
		}

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

		return isLinked;

	}

	/** update version; if changed - update master */
	private boolean processUpdate() {

		if (!configService.isIdentityValid()) {
			log.error("", new IllegalStateException(
					"identity must be valid in order to process update"));
			return false;
		}

		/** before update */
		final Config one = configService.getVersionConfig();

		configService.updateVersion();

		/** after update */
		final Config two = configService.getVersionConfig();

		final boolean hasOne = one.hasPath(PATH_VERSION);
		final boolean hasTwo = two.hasPath(PATH_VERSION);

		if (hasOne && hasTwo) {

			final String versionOne = one.getString(PATH_VERSION);
			final String versionTwo = two.getString(PATH_VERSION);

			/** either version change or we are pulling master */
			final boolean shouldUpdate = !versionOne.equals(versionTwo)
					|| versionTwo.equals("master");

			if (shouldUpdate) {
				log.info("master change : {} -> {}", versionOne, versionTwo);
				configService.updateMaster();
			} else {
				log.debug("no version change");
			}

			return true;

		} else {

			log.error("version invalid; \n\t one: {} \n\t two: {}", one, two);
			log.error("", new IllegalStateException(
					"versions must be valid in both old and new configuration"));

			return false;

		}

	}

	/** TODO restart karaf and/or jvm */
	private boolean processRestart() {
		try {
			// system.reboot();
			return true;
		} catch (final Exception e) {
			log.error("reboot failure", e);
			return false;
		}
	}

	/** submit both "instant" and "schedule" jobs */
	private void schedule(//
			final Config config, //
			final String jobName, //
			final Runnable jobTask //
	) {

		/** TODO */
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
					final String subName = jobName + "/instant";
					scheduler.removeJob(subName);
					scheduler.fireJobAt(subName, jobTask, null, instantDate);
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
				final String subName = jobName + "/schedule";
				scheduler.removeJob(subName);
				scheduler.addJob(subName, jobTask, null, schedule, true);
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
