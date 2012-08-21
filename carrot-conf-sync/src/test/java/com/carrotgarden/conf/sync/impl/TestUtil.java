/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.sync.impl;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtil {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPatternt() {

		{

			final String text = "2012-01-15T12:15:30Z";
			log.debug("text : {}", text);

			final Date date = Util.parseISO(text);
			log.debug("date : {}", date);

		}

		{

			final String text = "2012-01-15T12:15:30+05:00";
			log.debug("text : {}", text);

			final Date date = Util.parseISO(text);
			log.debug("date : {}", date);

		}

		{

			final String text = "2012-01-15T12:15:30";
			log.debug("text : {}", text);

			final Date date = Util.parseISO(text);
			log.debug("date : {}", date);

		}

	}

	@Test
	public void testDefault() {

		final Date date = Util.parseISO(null);
		log.debug("date : {}", date);

		assertEquals(0, date.getTime());

		assertTrue(date.before(new Date()));

	}

}
