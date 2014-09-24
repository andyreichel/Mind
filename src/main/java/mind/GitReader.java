package mind;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

public class GitReader implements SCMReader {
	BranchComparer branchComparer;
	GitApiImpl gitConnection;
	
	public GitReader(GitApiImpl gitConnection, BranchComparer branchComparer) throws IOException,
			InvalidRemoteException, TransportException, GitAPIException {
		this.branchComparer = branchComparer;
		this.gitConnection = gitConnection;
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
	
	public HashMap<Integer, String> getMapOfCheckinMessagesForResource(String resourceName)
	{
		
		return null;
	}
}
