/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.id.impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotgarden.conf.id.api.Constant;
import com.carrotgarden.conf.id.api.Identity;
import com.carrotgarden.conf.id.api.IdentityService;

public class TestIdentityFromFolderName {

	private final static Logger log = LoggerFactory
			.getLogger(TestIdentityFromFolderName.class);

	private final String key = "carrot.config.const.id.folder-domain-suffix";

	private String folder;

	private String suffix;

	@Before
	public void setUp() throws Exception {

		/** pretend this project name is an instance identity */
		folder = "carrot-conf-id";

		/** pretend this is a domain suffix */
		suffix = "-conf-id";

		System.setProperty(key, suffix);

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIdentity0() throws Exception {

		final Constant constValues = Util.constant();

		assertEquals(constValues.idFolderDomainSuffix(), suffix);

		final Identity identity = new IdentityFromFolderName(constValues);

		assertEquals(identity.getId(), folder);

		assertEquals(identity.getSource(), Identity.Source.PARENT_FOLDER_NAME);

	}

	@Test
	public void testIdentity1() throws Exception {

		final String prop = "carrot.config.active.identity";

		assertEquals(System.getProperty(prop), null);

		final IdentityService service = new IdentityServiceProvider();

		final Identity identity = service.getCurrentIdentity();

		assertEquals(identity.getId(), folder);

		assertEquals(System.getProperty(prop), folder);

	}

}
