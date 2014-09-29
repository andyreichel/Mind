package mind;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;

public interface GitApi {
	void cloneBranch(String branch) throws IOException, InvalidRemoteException, TransportException, GitAPIException;
	void setRepository(String branch) throws IOException;
	HashMap<String, List<String>> getCommitMessagesAndTouchedFilesForEachRevision(String branch) throws IOException, NoHeadException, GitAPIException;
	public Repository getRepository();
	public String getHeadBranch() throws NoSuchBranchException;
	public List<String> getConfiguredVersions();
	
}
