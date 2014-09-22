package mind;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

public class GitReader implements SCMReader {
	String gitUrl;

	Repository repository;

	public GitReader(Configuration config) throws IOException,
			InvalidRemoteException, TransportException, GitAPIException {
		String gitName = config.getString("git.name");
		String gitPw = config.getString("git.password");
		gitUrl = config.getString("git.url");

		CredentialsProvider cp;
		cp = new UsernamePasswordCredentialsProvider(gitName, gitPw);

		File localPath = File.createTempFile("TestGitRepository", "");
		System.out.println(localPath.getAbsolutePath());
		localPath.delete();

		CloneCommand cc = new CloneCommand().setCredentialsProvider(cp)
				.setDirectory(localPath).setURI(gitUrl).setBranch("master");
		Git git = cc.call();

		System.out.println(git.getRepository());

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder
				.setGitDir(new File(localPath.getAbsolutePath() + "/.git"))
				.readEnvironment().findGitDir().build();

		System.out.println(repository.getFullBranch());

		RevTree tree = getTree(repository);
		printFile(repository, tree);
		

	}

	private static void printFile(Repository repository, RevTree tree)
			throws MissingObjectException, IncorrectObjectTypeException,
			CorruptObjectException, IOException {
		// now try to find a specific file
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(true);
		treeWalk.setFilter(PathFilter.create("src/main/java/mind/GitReader.java"));
		if (!treeWalk.next()) {
			throw new IllegalStateException(
					"Did not find expected file 'GitReader.java'");
		}
		FileMode fileMode = treeWalk.getFileMode(0);
		ObjectLoader loader = repository.open(treeWalk.getObjectId(0));
		System.out.println("README.md: " + getFileMode(fileMode) + ", type: "
				+ fileMode.getObjectType() + ", mode: " + fileMode + " size: "
				+ loader.getSize());
	}

	private static String getFileMode(FileMode fileMode) {
		if (fileMode.equals(FileMode.EXECUTABLE_FILE)) {
			return "Executable File";
		} else if (fileMode.equals(FileMode.REGULAR_FILE)) {
			return "Normal File";
		} else if (fileMode.equals(FileMode.TREE)) {
			return "Directory";
		} else if (fileMode.equals(FileMode.SYMLINK)) {
			return "Symlink";
		} else {
			// there are a few others, see FileMode javadoc for details
			throw new IllegalArgumentException(
					"Unknown type of file encountered: " + fileMode);
		}
	}

	private static RevTree getTree(Repository repository)
			throws AmbiguousObjectException, IncorrectObjectTypeException,
			IOException, MissingObjectException {
		ObjectId lastCommitId = repository.resolve(Constants.HEAD);
		// a RevWalk allows to walk over commits based on some filtering
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(lastCommitId);
		System.out.println("Time of commit (seconds since epoch): "
				+ commit.getCommitTime());
		// and using commit's tree find the path
		RevTree tree = commit.getTree();
		System.out.println("Having tree: " + tree);
		return tree;
	}

	public int getSizeOfClass(String version, String className)
			throws IOException {
		return 0;
	}

	public int getNumberOfDefectsRelatedToClass(String version,
			String className, IssueTrackerReader itReader) {
		return 0;
	}

	public int getNumberOfLOCtouched(String version1, String version2, String className, BranchComparer fileComparer) {
		return 0;
	}

}
