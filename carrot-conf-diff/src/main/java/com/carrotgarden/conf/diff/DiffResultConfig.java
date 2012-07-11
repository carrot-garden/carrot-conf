/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.diff;

import com.typesafe.config.Config;

/** #Config comparison result */
public final class DiffResultConfig {

	private final Config delete;
	private final Config insert;
	private final Config updateFrom;
	private final Config updateInto;
	private final Config updateNone;

	/** keep package private */
	DiffResultConfig() {
		this(//
				ConfigDiff.emptyConfig(), //
				ConfigDiff.emptyConfig(), //
				ConfigDiff.emptyConfig(), //
				ConfigDiff.emptyConfig(), //
				ConfigDiff.emptyConfig() //
		);
	}

	/** keep package private */
	DiffResultConfig(//
			final Config delete, //
			final Config insert, //
			final Config updateFrom, //
			final Config updateInto, //
			final Config updateNone //
	) {
		this.delete = delete;
		this.insert = insert;
		this.updateFrom = updateFrom;
		this.updateInto = updateInto;
		this.updateNone = updateNone;
	}

	/** part of configuration that was strictly removed */
	public Config getDelete() {
		return delete;
	}

	/** part of configuration that was strictly added */
	public Config getInsert() {
		return insert;
	}

	/** part of configuration that was updated; old values */
	public Config getUpdateFrom() {
		return updateFrom;
	}

	/** part of configuration that was updated; new values */
	public Config getUpdateInto() {
		return updateInto;
	}

	/** part of configuration that was not affected by the update */
	public Config getUpdateNone() {
		return updateNone;
	}

	/** do we have strict removals? */
	public boolean hasDelete() {
		return !delete.isEmpty();
	}

	/** do we have strict additions? */
	public boolean hasInsert() {
		return !insert.isEmpty();
	}

	/** do we have updated values? */
	public boolean hasUpdateDiff() {
		return !updateFrom.isEmpty() && !updateInto.isEmpty();
	}

	/** do we have unchanged values? */
	public boolean hasUpdateNone() {
		return !updateNone.isEmpty();
	}

	/** print components of comparison */
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
