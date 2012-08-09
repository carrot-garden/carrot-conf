/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.karaf.impl;

import java.util.Calendar;
import java.util.Date;

public class Util {

	/**
	 * form 2012-01-15T12:15:30Z
	 */
	public static Date parseISO(final String text) {

		try {

			final Calendar calendar = javax.xml.bind.DatatypeConverter
					.parseDateTime(text);

			return calendar.getTime();

		} catch (final Exception e) {

			return new Date(0);

		}

	}

}
