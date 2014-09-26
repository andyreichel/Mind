package mind;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;


public interface SCMReader {
	public int getNumberOfLOCtouched(String currentVersion, String previousVersion, String className) throws IOException, NoSuchBranchException;
	public BranchComparer getBranchComparer();
	public HashMap<String, List<String>> getCommitMessagesAndTouchedFilesForEachRevision(String branch) throws IOException, NoHeadException, GitAPIException;
	public String getHeadBranch() throws NoSuchBranchException;
}
