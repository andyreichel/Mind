package mind;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;

public class GitReader implements SCMReader {
	BranchComparer branchComparer;
	GitApi gitApi;
	
	public GitReader(GitApi gitApi, BranchComparer branchComparer) throws IOException,
			InvalidRemoteException, TransportException, GitAPIException {
		this.branchComparer = branchComparer;
		this.gitApi = gitApi;
	}

	public int getNumberOfLOCtouched(String branchName1, String branchName2, String classId) throws IOException, NoSuchBranchException {
		HashMap<String, Integer> mapWithNumberOfChangesPerResource = branchComparer.getMapWithNumberOfChangesPerResource(branchName1, branchName2);
		if(!mapWithNumberOfChangesPerResource.containsKey(classId))
			return 0;
		return mapWithNumberOfChangesPerResource.get(classId);
	}
	
	
	public HashMap<String, List<String>> getCommitMessagesAndTouchedFilesForEachRevision(String branch) throws IOException, NoHeadException, GitAPIException
	{
		return gitApi.getCommitMessagesAndTouchedFilesForEachRevision(branch);
	}

	public BranchComparer getBranchComparer() {
		return branchComparer;
	}

	public String getHeadBranch() throws NoSuchBranchException {
		return gitApi.getHeadBranch();
	}

	public List<String> getConfiguredVersions() {
		return gitApi.getConfiguredVersions();
	}
}
