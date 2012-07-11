/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.diff;

import com.typesafe.config.ConfigObject;

/** #ConfigObject comparison result */
public final class DiffResultObject {

	private final ConfigObject delete;
	private final ConfigObject insert;
	private final ConfigObject updateFrom;
	private final ConfigObject updateInto;
	private final ConfigObject updateNone;

	DiffResultObject() {
		this(//
				ConfigDiff.emptyObject(), //
				ConfigDiff.emptyObject(), //
				ConfigDiff.emptyObject(), //
				ConfigDiff.emptyObject(), //
				ConfigDiff.emptyObject() //
		);
	}

	DiffResultObject(final ConfigObject delete, final ConfigObject insert,
			final ConfigObject updateFrom, final ConfigObject updateInto,
			final ConfigObject updateNone) {
		this.delete = delete;
		this.insert = insert;
		this.updateFrom = updateFrom;
		this.updateInto = updateInto;
		this.updateNone = updateNone;
	}

	public ConfigObject getDelete() {
		return delete;
	}

	public ConfigObject getInsert() {
		return insert;
	}

	public ConfigObject getUpdateFrom() {
		return updateFrom;
	}

	public ConfigObject getUpdateInto() {
		return updateInto;
	}

	public ConfigObject getUpdateNone() {
		return updateNone;
	}

	public boolean hasDelete() {
		return !delete.isEmpty();
	}

	public boolean hasInsert() {
		return !insert.isEmpty();
	}

	public boolean hasUpdateDiff() {
		return !updateFrom.isEmpty() && !updateInto.isEmpty();
	}

	public boolean hasUpdateNone() {
		return !updateNone.isEmpty();
	}

	@Override
	public String toString() {

		final StringBuilder text = new StringBuilder(256);

		text.append("\n");
		text.append("delete : ");
		text.append(getDelete());

		text.append("\n");
		text.append("insert : ");
		text.append(getInsert());

		text.append("\n");
		text.append("update from : ");
		text.append(getUpdateFrom());

		text.append("\n");
		text.append("update into : ");
		text.append(getUpdateInto());

		text.append("\n");
		text.append("update none : ");
		text.append(getUpdateNone());

		return text.toString();

	}

}
