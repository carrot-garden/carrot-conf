package com.carrotgarden.conf.base.impl;

import java.io.File;

public interface RepoService {

	/** location of parent bare repo */
	File getLocalArchon();

	/** location of "version" workspace repo */
	File getLocalVersion();

	/** location of "master" workspace repo */
	File getLocalMaster();

	/** generate parent and child repos */
	boolean ensureRepoAll();

	/** parent bare repo */
	boolean ensureRepoArchon();

	/** child repo for branch "version" workspace */
	boolean ensureRepoVersion();

	/** child repo for branch "master" workspace */
	boolean ensureRepoMaster();

	/** fetch latest parent repo from remote origin */
	boolean updateRepoArchon();

	/** fetch latest "version" workspace from "archon" */
	boolean updateRepoVersion();

	/** fetch latest "master" workspace from "archon" for a given version tag */
	boolean updateRepoMaster(final String version);

	/** cleanup all repos */
	boolean deleteRepoAll();

}
