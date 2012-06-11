package com.carrotgarden.conf.base.api;

import static org.testng.AssertJUnit.*;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.carrotgarden.conf.base.impl.IdentityServiceProvider;

public class TestConfigService {

	@BeforeTest
	protected void setUp() throws Exception {

		System.setProperty(ConfigConst.Id.SYSTEM_PROPERTY, "karaf.company.com");

	}

	@AfterTest
	protected void tearDown() throws Exception {
	}

	@Test
	public void testGetIdentity() {

		final IdentityService service = new IdentityServiceProvider();

		final Identity identity = service.getCurrentIdentity();

		assertTrue(identity.isAvailable());

		assertEquals(identity.getId(), "karaf.company.com");

	}

}
