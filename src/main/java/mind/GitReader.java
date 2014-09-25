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
	GitApiImpl gitApi;
	
	public GitReader(GitApiImpl gitApi, BranchComparer branchComparer) throws IOException,
			InvalidRemoteException, TransportException, GitAPIException {
		this.branchComparer = branchComparer;
		this.gitApi = gitApi;
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
		if(!mapWithNumberOfChangesPerResource.containsKey(ResourceUtils.stripOfClassNameFromClassId(classId)))
			return 0;
		return mapWithNumberOfChangesPerResource.get(ResourceUtils.stripOfClassNameFromClassId(classId));
	}
	
	
	public HashMap<String, List<String>> getCommitMessagesAndTouchedFilesForEachRevision(String branch) throws IOException, NoHeadException, GitAPIException
	{
		return gitApi.getCommitMessagesAndTouchedFilesForEachRevision(branch);
	}

	public BranchComparer getBranchComparer() {
		return branchComparer;
	}
}
