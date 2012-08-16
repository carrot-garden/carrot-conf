/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package bench.ssl;

public class MainGoogle {

	public static void main(final String[] a) throws Exception {

		final java.net.URLConnection c = new java.net.URL("https://google.com/")
				.openConnection();

		c.setDoOutput(true);

		c.getOutputStream();

	}

}
