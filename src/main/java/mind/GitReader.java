package mind;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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
	BranchComparer branchComparer;

	Repository repository;

	public GitReader(Configuration config, BranchComparer branchComparer) throws IOException,
			InvalidRemoteException, TransportException, GitAPIException {
		initGitConnection(config);
		this.branchComparer = branchComparer;
	}
	
	private void initGitConnection(Configuration config) throws IOException, InvalidRemoteException, TransportException, GitAPIException
	{
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
	}

	public int getSizeOfClass(String version, String className)
			throws IOException {
		return 0;
	}

	public int getNumberOfDefectsRelatedToClass(String version,
			String className, IssueTrackerReader itReader) {
		return 0;
	}

	public int getNumberOfLOCtouched(String branchName1, String branchName2, String classId) throws IOException {
		HashMap<String, Integer> mapWithNumberOfChangesPerResource = branchComparer.getMapWithNumberOfChangesPerResource(branchName1, branchName2);
		if(!mapWithNumberOfChangesPerResource.containsKey(stripOfClassNameFromClassId(classId)))
			return 0;
		return mapWithNumberOfChangesPerResource.get(stripOfClassNameFromClassId(classId));
	}
	
	private static String stripOfClassNameFromClassId(String classId)
	{
		int slashIndex = classId.lastIndexOf("/");
		if(slashIndex == -1)
			return classId;
		return classId.substring(slashIndex);
	}

	public BranchComparer getBranchComparer() {
		return branchComparer;
	}
}
