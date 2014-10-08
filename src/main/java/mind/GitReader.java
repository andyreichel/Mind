package mind;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;

import exceptions.NoSuchBranchException;
import externalinterfaces.BranchComparer;
import externalinterfaces.GitApi;
import externalinterfaces.SCMReader;

/**
 * Class that provides methods to read information from git and interprets them
 * 
 *
 */
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
		
		int projectKeySignIndex = classId.indexOf(":");
		if(projectKeySignIndex == -1)
		{
			return 0;
		}
		
		String projectKeyStrippedOfResourceKey = classId.substring(projectKeySignIndex+1);
		if(!mapWithNumberOfChangesPerResource.containsKey(projectKeyStrippedOfResourceKey))
		{
			return 0;
		}
		return mapWithNumberOfChangesPerResource.get(projectKeyStrippedOfResourceKey);
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
