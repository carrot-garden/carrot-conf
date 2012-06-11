package com.carrotgarden.conf.base.impl;

import static org.testng.AssertJUnit.*;

import java.io.File;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.carrotgarden.conf.base.api.ConfigConst;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestUtil {

	private static final Logger log = LoggerFactory.getLogger(TestUtil.class);

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	@Test
	public void testFileAsString1() throws Exception {

		final String name = UUID.randomUUID().toString();

		final String file = "./target/" + name;

		final String textSave = UUID.randomUUID().toString() + "\n";

		Util.saveFileAsString(file, textSave);

		final String textLoad = Util.loadFileAsString(file);

		new File(file).delete();

		assertEquals(textSave, textLoad);

	}

	static final String TEXT = ""
			+ "A journey of a thousand miles begins with a single step" + "\n"
			+ "千里之行，始于足下」 老子" + "\n" + "qiān lǐ zhī xíng，shǐ yú zú xià" + "\n"
			+ "Un voyage de mille miles commence par une seule étape" + "\n"
			+ "Путь в тысячу миль начинается с одного шага" + "\n"
			+ "Eine Reise von tausend Meilen beginnt mit dem ersten Schritt"
			+ "\n";

	@Test
	public void testFileAsString2() throws Exception {

		final String file = "./src/test/resources/test.file";

		final String text = Util.loadFileAsString(file);

		assertEquals(text, TEXT);

	}

	@Test
	public void testUrlAsString() throws Exception {

		final String host = "www.google.com";

		final String url = "http://" + host;

		final String text = Util.loadUrlAsString(url);

		assertTrue(text.contains(host));

	}

	@Test
	public void testOverride() {

		final Config boot = ConfigFactory.load(ConfigConst.Repo.BOOT_FILE);
		final Config tree = boot.getConfig(ConfigConst.Key.REPOSITORY);

		final Properties properties = new Properties();
		properties.put("local", "override");

		final Config prop = ConfigFactory.parseProperties(properties);

		final Config conf = prop.withFallback(tree);

		log.debug("conf : {}", conf);

		assertEquals(conf.getString("local"), "override");

	}

	@Test
	public void testPath() {

		assertEquals(Util.getInstancePathFromInstanceId(null),
				ConfigConst.Repo.DIR_INSTANCE);

		assertEquals(Util.getInstancePathFromInstanceId(""),
				ConfigConst.Repo.DIR_INSTANCE);

		assertEquals(Util.getInstancePathFromInstanceId("karaf-company-com"),
				ConfigConst.Repo.DIR_INSTANCE + "/karaf-company-com");

		assertEquals(Util.getInstancePathFromInstanceId("karaf.company.com"),
				ConfigConst.Repo.DIR_INSTANCE + "/com/company/karaf");

		assertEquals(
				Util.getInstancePathFromInstanceId("123.karaf.company.com"),
				ConfigConst.Repo.DIR_INSTANCE + "/com/company/karaf/123");

	}

	@Test
	public void testTrim() {

		assertEquals(
				Util.getInstancePathTrimLast(ConfigConst.Repo.DIR_INSTANCE),
				ConfigConst.Repo.DIR_INSTANCE);

		assertEquals(
				Util.getInstancePathTrimLast(ConfigConst.Repo.DIR_INSTANCE
						+ "/trim"), ConfigConst.Repo.DIR_INSTANCE);

		assertEquals(
				Util.getInstancePathTrimLast(ConfigConst.Repo.DIR_INSTANCE
						+ "/trim/more"), ConfigConst.Repo.DIR_INSTANCE
						+ "/trim");

	}

}
