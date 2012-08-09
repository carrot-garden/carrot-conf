/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package bench.git;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class MainGit {

	static final Logger log = LoggerFactory.getLogger(MainGit.class);

	private static final String USER = "";
	private static final String PASS = "";

	private final static CredentialsProvider CRED_PROV = //
	new UsernamePasswordCredentialsProvider(USER, PASS);

	final static Config CONF = ConfigFactory.load("config-tester");

	private static final String LOCAL = //
	CONF.getString("carrot.config.repository.local");

	private static final String REMOTE = //
	CONF.getString("carrot.config.repository.remote");

	private static final int TIMEOUT = //
	CONF.getMilliseconds("carrot.config.repository.timeout").intValue() / 1000;

	private static final File getLocal() {
		return new File(LOCAL);
	}

	public static void main(final String[] args) throws Exception {

		log.debug("init");

		log.debug("CONF : " + CONF);
		log.debug("LOCAL : " + LOCAL);
		log.debug("REMOTE : " + REMOTE);
		log.debug("TIMEOUT : " + TIMEOUT);

		final FileRepositoryBuilder builder = new FileRepositoryBuilder();
		builder.setGitDir(getLocal());
		builder.readEnvironment();
		builder.findGitDir();

		final Repository repo = builder.build();

		// logger.debug("branch=" + repo.getBranch());
		// logger.debug("state=" + repo.getRepositoryState());

		switch (repo.getRepositoryState()) {
		case BARE:
			break;
		case SAFE:
			break;
		default:
			log.error("wrong state");
		}

		makeCloneBare();

		makeCloneBranch("master");

		makeCloneBranch("version");

		// git.fetch().setTimeout(TIMEOUT).call();

		// git.checkout().setName(Constants.MASTER).call();
		// git.pull().setTimeout(10).call();

		// git.checkout().setName("current-config").call();

		// log.debug("branch=" + git.getRepository().getBranch());

		// runClone();

		// final Git git = Git.open(FILE);
		// final StatusCommand status = git.status();
		// final Status result = status.call();
		// logger.debug("result=" + result);

		// final Git git = new Git(repo);

		// final RevCommit commit =
		// git.commit().setMessage("test commit").call();

		// final RevTag tag = git.tag().setName("test_tag").call();

		// logger.debug("branch=" + repo.getBranch());
		// logger.debug("state=" + repo.getRepositoryState());

		log.debug("done");

	}

	static Git makeCloneBare() throws Exception {

		final CloneCommand clone = Git.cloneRepository();
		clone.setBare(true);
		clone.setDirectory(getLocal());
		clone.setURI(REMOTE);
		clone.setTimeout(TIMEOUT);

		final Git git = clone.call();

		final ListBranchCommand lister = git.branchList();
		final List<Ref> list = lister.call();
		for (final Ref ref : list) {
			log.debug("ref = " + ref);
		}

		// git.fetch();

		return null;

	}

	static Git makeCloneBranch(final String branch) throws Exception {

		final String branchDir = getLocal().getAbsolutePath() + "." + branch;

		final File local = new File(branchDir);
		final String remote = getLocal().toURI().toString();

		final CloneCommand clone = Git.cloneRepository();
		clone.setBranch("refs/heads/" + branch);
		clone.setDirectory(local);
		clone.setURI(remote);

		final Git git = clone.call();

		// git.fetch();

		// final ListBranchCommand lister = git.branchList();
		// final List<Ref> list = lister.call();
		// for (final Ref ref : list) {
		// log.debug("ref = " + ref);
		// }

		// git.checkout().setName(branch).call();

		return null;

	}

	static Git makeOpen() throws Exception {

		return Git.open(getLocal());

	}

	final ProgressMonitor monitor = new ProgressMonitor() {

		@Override
		public void start(final int totalTasks) {
			log.debug("start : " + totalTasks);
		}

		@Override
		public void beginTask(final String title, final int totalWork) {
			log.debug("beginTask : " + title);
		}

		@Override
		public void update(final int completed) {
			log.debug("update : " + completed);
		}

		@Override
		public void endTask() {
			log.debug("endTask : ");
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

	};

}
