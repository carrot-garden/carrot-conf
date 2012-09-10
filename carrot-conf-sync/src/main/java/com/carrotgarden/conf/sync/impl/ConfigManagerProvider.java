/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.sync.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.event.ConfigEvent;
import com.carrotgarden.conf.file.FileLink;
import com.carrotgarden.conf.file.FileWatch;
import com.carrotgarden.conf.id.api.Identity;
import com.carrotgarden.conf.repo.api.ConfigService;
import com.carrotgarden.conf.sync.api.ConfigManager;
import com.carrotgarden.osgi.event.api.EventAdminService;
import com.typesafe.config.Config;

/**
 * 
 */
@Component(immediate = true, name = ConfigManager.SERVICE_PID)
public class ConfigManagerProvider implements ConfigManager {

	private final static Logger log = LoggerFactory
			.getLogger(ConfigManagerProvider.class);

	private final static String PATH_CALENDAR = "carrot.config.calendar";
	private final static String PATH_VERSION = "carrot.config.version";

	private final static String KEY_IDENTITY = "identity";
	private final static String KEY_RESTART = "restart";
	private final static String KEY_VERSION = "version";

	private final static String KEY_INSTANT = "instant";
	private final static String KEY_SCHEDULE = "schedule";

	/** job name convention */
	private static String name(final String name) {
		return ConfigManagerProvider.class.getName() + "/" + name;
	}

	/** job sub name convention */
	private static String name(final String name, final String subName) {
		return name + "-" + subName;
	}

	/** unique job root names */
	private static final String JOB_ACTIVATE = name("activate");
	private static final String JOB_TESTING = name("testing");
	//
	private static final String JOB_IDENTITY = name(KEY_IDENTITY);
	private static final String JOB_RESTART = name(KEY_RESTART);
	private static final String JOB_VERSION = name(KEY_VERSION);

	private boolean isActive;

	/**
	 * guard against job invocation that can happen after component is
	 * deactivated
	 */
	private boolean isActive() {
		return isActive;
	}

	/** runs once on component activation */
	private final Runnable jobActivate = new Runnable() {
		@Override
		public void run() {

			log.debug("activate");

			final boolean isSuccess = isActive() //
					&& processCalendar(true) // default schedule
					&& processActivate() //
					&& processLink() //
					&& processCalendar(false) // instance schedule
			;

			if (isSuccess) {
				log.info("config activate success");
				eventer.post(ConfigEvent.CONFIG_CHANGE);
				scheduleTesting();
			} else {
				log.error("config activate failure", new Exception());
			}

		}
	};

	/** runs on "identity" calendar */
	private final Runnable jobIdentity = new Runnable() {
		@Override
		public void run() {

			log.debug("identity");

			final boolean isSuccess = isActive() //
					&& processIdentity() //
			;

			if (isSuccess) {
				eventer.post(ConfigEvent.JOB_INDENTITY_SUCCESS);
			} else {
				eventer.post(ConfigEvent.JOB_INDENTITY_FAILURE);
			}

		}
	};

	/** runs on "restart" calendar */
	private final Runnable jobRestart = new Runnable() {
		@Override
		public void run() {

			log.debug("restart");

			final boolean isSuccess = isActive() //
					&& processRestart() //
			;

			if (isSuccess) {
				eventer.post(ConfigEvent.JOB_RESTART_SUCCESS);
			} else {
				eventer.post(ConfigEvent.JOB_RESTART_FAILURE);
			}

		}
	};

	/** runs on "version" calendar */
	private final Runnable jobVersion = new Runnable() {
		@Override
		public void run() {

			log.debug("version");

			final boolean isSuccess = isActive() //
					&& processUpdate() //
					&& processLink() //
					&& processCalendar(false) // instance calendar
			;

			if (isSuccess) {
				eventer.post(ConfigEvent.JOB_VERSION_SUCCESS);
			} else {
				eventer.post(ConfigEvent.JOB_VERSION_FAILURE);
			}

		}
	};

	/** initial configuration download, if available */
	private boolean processActivate() {

		return true //
				&& configService.updateIdentity() //
				&& configService.updateVersion() //
				&& configService.updateMaster() //
		;

	}

	/** re-discover instance identity */
	private boolean processIdentity() {

		return configService.updateIdentity();

	}

	/** re-schedule all config jobs */
	private boolean processCalendar(final boolean isDefault) {

		final Config config;

		final Config reference = Util.reference();

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

		if (!configService.updateVersion()) {
			log.error("", new Exception("version update failure"));
			return false;
		}

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
				if (configService.updateMaster()) {
					log.info("master change succress: {} -> {}", //
							versionOne, versionTwo);
					eventer.post(ConfigEvent.CONFIG_CHANGE);
				} else {
					log.error("master change failure: {} -> {}", //
							versionOne, versionTwo);
				}
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
			final String instant = config.getString(KEY_INSTANT);

			log.debug("instant : {}/{}", jobName, instant);

			// final Date instantDate = new Date(System.currentTimeMillis() + 10
			// * 1000);

			final Date currentDate = new Date();
			final Date instantDate = Util.parseISO(instant);

			if (currentDate.before(instantDate)) {
				try {
					final String subName = name(jobName, KEY_INSTANT);
					scheduler.removeJob(subName);
					scheduler.fireJobAt(subName, jobTask, null, instantDate);
				} catch (final Exception e) {
					log.error("schedule failure", e);
				}
			}
		}

		/** cron pattern "schedule" job */
		{
			// final String schedule = "0 0 0 * * ?";//
			// config.getString("schedule");

			final String schedule = config.getString(KEY_SCHEDULE);

			log.debug("schedule : {}/{}", jobName, schedule);

			try {
				final String subName = name(jobName, KEY_SCHEDULE);
				scheduler.removeJob(subName);
				scheduler.addJob(subName, jobTask, null, schedule, true);
			} catch (final Exception e) {
				log.error("schedule failure", e);
			}

		}

	}

	private ComponentContext context;

	@Activate
	protected void activate(final ComponentContext c) {

		context = c;

		log.debug("activate");

		try {
			scheduler.fireJob(jobActivate, null);
		} catch (final Exception e) {
			log.error("schedule failure", e);
		}

		isActive = true;

	}

	@Modified
	protected void modified() {

		scheduleTesting();

	}

	@Deactivate
	protected void deactivate() {

		isActive = false;

		log.debug("deactivate");

		try {

			scheduler.removeJob(JOB_ACTIVATE);
			scheduler.removeJob(JOB_TESTING);

			unschedule(JOB_IDENTITY);
			unschedule(JOB_VERSION);
			unschedule(JOB_RESTART);

		} catch (final Exception e) {
			log.error("unschedule failure", e);
		}

		context = null;

	}

	/** runs periodically when testing is enabled */
	private final Runnable jobTesting = new Runnable() {
		@Override
		public void run() {

			log.debug("testing");

			if (!isActive()) {
				return;
			}

			final List<File> list = FileWatch.pathCheck(getMasterInstance());

			if (list.isEmpty()) {
				return;
			}

			log.info("master change : {}", list);

			final Map<String, String> props = Util
					.wrap(context.getProperties());

			final String configFile = props.get(PROP_TEST_CONFIG);

			if (configFile == null) {
				/** re-parse existing application.conf */
				configService.injectMaster(null);
			} else {
				/** re-parse provided application.conf */
				configService.injectMaster(new File(configFile));
			}

			eventer.post(ConfigEvent.CONFIG_CHANGE);

		}
	};

	private File getMasterInstance() {
		return configService.getMasterInstance();
	}

	/** setup periodic master repo change detection task */
	private void scheduleTesting() {

		try {

			final Map<String, String> props = Util
					.wrap(context.getProperties());

			final String enable = props.get(PROP_TEST_ENABLE);

			final long period = Util.parseLong(props.get(PROP_TEST_PERIOD), 3);

			if (enable == null) {
				return;
			}

			if ("true".equalsIgnoreCase(enable)) {

				FileWatch.pathAdd(getMasterInstance());

				scheduler.addPeriodicJob( //
						JOB_TESTING, jobTesting, null, period, true);

			} else {

				scheduler.removeJob(JOB_TESTING);

				FileWatch.pathRemove(getMasterInstance());

			}

			log.info("schedule testing; \n\t enable={} \n\t period={}", //
					enable, period);

		} catch (final Exception e) {
			log.error("testing failure", e);
		}

	}

	private void unschedule(final String jobName) {

		scheduler.removeJob(name(jobName, KEY_INSTANT));
		scheduler.removeJob(name(jobName, KEY_SCHEDULE));

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

	private EventAdminService eventer;

	@Reference
	protected void bind(final EventAdminService s) {
		eventer = s;
	}

	protected void unbind(final EventAdminService s) {
		eventer = null;
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

	@Override
	public boolean isIdentityValid() {
		return configService.isIdentityValid();
	}

	@Override
	public Identity getIdentity() {
		return configService.getIdentity();
	}

	@Override
	public boolean isConfigValid() {
		return configService.isMasterValid();
	}

	@Override
	public Config getConfig() {
		return configService.getMasterConfig();
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
