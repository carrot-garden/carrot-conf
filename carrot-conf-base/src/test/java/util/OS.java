package util;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * http://quirkygba.blogspot.com/2009/11/setting-environment-variables-in-java.
 * html
 * */
public class OS {

	private final static Logger log = LoggerFactory.getLogger(OS.class);

	public interface LibC extends Library {

		String getenv(String name);

		int setenv(String name, String value, int overwrite);

		int unsetenv(String name);

	}

	public static final LibC LIB_C = (LibC) Native.loadLibrary("c", LibC.class);

	public static void main(final String[] args) {

		log.debug("init");

		final String name = UUID.randomUUID().toString();

		log.debug("test0 = " + LIB_C.getenv(name));

		LIB_C.setenv(name, "test1", 1);
		log.debug("test1 = " + LIB_C.getenv(name));

		LIB_C.setenv(name, "test2", 1);
		log.debug("test2 = " + LIB_C.getenv(name));

		log.debug("done");

	}

}
