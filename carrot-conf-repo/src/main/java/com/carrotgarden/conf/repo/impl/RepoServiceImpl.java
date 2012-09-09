/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.conf.repo.impl;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;

/** git repo manager for a specific configuration repo layout */
public class RepoServiceImpl implements RepoService {

	private static final Logger log = LoggerFactory
			.getLogger(RepoServiceImpl.class);

	public static final String GIT = "git";
	public static final String MASTER = "master";
	public static final String VERSION = "version";

	private final String local; // file url
	private final String remote; // git uri
	private final int timeout; // seconds

	public RepoServiceImpl(final Config conf) {

		local = conf.getString("local");

		remote = conf.getString("remote");

		timeout = conf.getMilliseconds("timeout").intValue() / 1000;

	}

	@Override
	public File getLocalArchon() {
		return getLocal(GIT);
	}

	@Override
	public File getLocalMaster() {
		return getLocal(MASTER);
	}

	@Override
	public File getLocalVersion() {
		return getLocal(VERSION);
	}

	protected File getLocal(final String branch) {
		return new File(local + "." + branch);
	}

	protected String getRefsHeads(final String branch) {
		return Constants.R_HEADS + branch;
	}

	protected String getRefsRemotes(final String origin, final String branch) {
		return Constants.R_REMOTES + origin + "/" + branch;
	}

	protected String getRefsTags(final String tag) {
		return Constants.R_TAGS + tag;
	}

	protected void assertRepoArchonHasRequiredBranches(final Git git)
			throws Exception {

		final List<Ref> branchList = git.branchList().call();

		boolean isMasterPresent = false;
		boolean isVersionPresent = false;

		final String master = getRefsHeads(MASTER);
		final String version = getRefsHeads(VERSION);

		for (final Ref branch : branchList) {

			final String name = branch.getName();

			if (master.equals(name)) {
				isMasterPresent = true;
			}

			if (version.equals(name)) {
				isVersionPresent = true;
			}

		}

		if (isMasterPresent && isVersionPresent) {
			return;
		} else {
			throw new Exception("missing required branches");
		}

	}

	@Override
	public boolean ensureRepoAll() {

		final boolean isReady = true //
				&& ensureRepoArchon() //
				&& ensureRepoVersion() //
				&& ensureRepoMaster() //
		;

		return isReady;

	}

	@Override
	public boolean ensureRepoArchon() {

		try {

			log.debug("trying open : archon");

			final Git git = Git.open(getLocalArchon());

			assertRepoArchonHasRequiredBranches(git);

			log.debug("open success : archon");

			return true;

		} catch (final Exception e) {

			log.debug("open failure : archon");

		}

		try {

			log.debug("trying clone : archon");

			final CloneCommand clone = Git.cloneRepository();

			clone.setBare(true);
			clone.setDirectory(getLocalArchon());
			clone.setURI(remote);
			clone.setTimeout(timeout);

			clone.call();

			final Git git = Git.open(getLocalArchon());

			assertRepoArchonHasRequiredBranches(git);

			log.debug("clone success : archon");

			return true;

		} catch (final Exception e) {

			log.error("clone failure", e);

		}

		return false;

	}

	@Override
	public boolean ensureRepoMaster() {
		return ensureRepoBranch(MASTER);
	}

	@Override
	public boolean ensureRepoVersion() {
		return ensureRepoBranch(VERSION);
	}

	protected String getLocalArchonURI() {
		return getLocalArchon().toURI().toString();
	}

	protected boolean ensureRepoBranch(final String branch) {

		if (!ensureRepoArchon()) {
			return false;
		}

		final File local = getLocal(branch);
		final String remote = getLocalArchonURI();

		try {

			log.debug("trying open : {}", branch);

			Git.open(local);

			log.debug("open success : {}", branch);

			return true;

		} catch (final Exception e) {

			log.debug("open failure : {}", branch);

		}

		try {

			log.debug("trying clone : {}", branch);

			final CloneCommand clone = Git.cloneRepository();
			clone.setBare(false);
			clone.setBranch(getRefsHeads(branch));
			clone.setDirectory(local);
			clone.setURI(remote);
			clone.setNoCheckout(true);
			clone.call();

			log.debug("clone success : {}", branch);

			return true;

		} catch (final Exception e) {

			log.error("clone failure", e);

		}

		return false;

	}

	@Override
	public boolean updateRepoVersion() {
		return updateRepoBranch(VERSION);
	}

	protected boolean isBranchPresent(final Git git, final String branch)
			throws Exception {

		final List<Ref> branchRefList = git.branchList().call();

		for (final Ref branchRef : branchRefList) {
			if (branchRef.getName().equals(getRefsHeads(branch))) {
				return true;
			}
		}

		return false;

	}

	protected boolean updateRepoBranch(final String branch) {

		if (!ensureRepoBranch(branch)) {
			return false;
		}

		try {

			log.debug("trying update : {}", branch);

			final Git git = Git.open(getLocal(branch));

			// git.fetch().setRemote("origin").call();

			git.branchCreate().setName(getRefsHeads(branch))
					.setStartPoint(getRefsRemotes("origin", branch))
					.setForce(true).call();

			git.checkout().setName(getRefsHeads(branch)).call();

			git.pull().call();

			log.debug("update success : {}", branch);

			return true;

		} catch (final Exception e) {

			log.error("update failure", e);

		}

		return false;

	}

	protected void asasertRepoTag(final Git git, final String tag)
			throws Exception {

		final List<Ref> tagList = git.tagList().call();

		for (final Ref revTag : tagList) {
			if (revTag.getLeaf().getName().equals(getRefsTags(tag))) {
				return;
			}
		}

		throw new Exception("missing tag=" + tag);

	}

	@Override
	public boolean updateRepoMaster(final String version) {
		return updateRepoBranchTag(MASTER, version);
	}

	protected boolean updateRepoBranchTag(final String branch, final String tag) {

		if (!ensureRepoBranch(branch)) {
			return false;
		}

		try {

			log.debug("trying update : {}/{}", branch, tag);

			final Git git = Git.open(getLocal(branch));

			git.fetch().setRemote("origin").call();

			git.checkout().setName(getRefsTags(tag)).call();

			// git.pull().call();

			log.debug("update success : {}/{}", branch, tag);

			return true;

		} catch (final Exception e) {

			log.error("update failure", e);

		}

		return false;

	}

	@Override
	public boolean updateRepoArchon() {

		if (!ensureRepoArchon()) {
			return false;
		}

		try {

			log.debug("trying update : archon");

			final Git git = Git.open(getLocalArchon());

			git.fetch().setTimeout(timeout).setRemote("origin").call();

			log.debug("update success : archon");

			return true;

		} catch (final Exception e) {

			log.error("update failure", e);

		}

		return false;

	}

	@Override
	public boolean deleteRepoAll() {

		final boolean isDeleted = true //
				&& Util.deleteFiles(getLocalMaster()) //
				&& Util.deleteFiles(getLocalVersion()) //
				&& Util.deleteFiles(getLocalArchon()) //
		;

		return isDeleted;

	}

}
