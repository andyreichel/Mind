package externalinterfaces;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;

import exceptions.NoSuchBranchException;

/**
 * Classes that implement this interface are able to communicate with a git repository and reads information from it 
 *
 */
public interface GitApi {
	/**
	 * clones a given branch to a local working dir
	 * @param branch
	 * @throws IOException
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws GitAPIException
	 */
	void cloneBranch(String branch) throws IOException, InvalidRemoteException, TransportException, GitAPIException;
	
	/**
	 * sets the current working dir to a branch. This branch has to be checked out in advance
	 * @param branch
	 * @throws IOException
	 */
	void setRepository(String branch) throws IOException;
	
	/**
	 * returns a map that relates commit messages of a revision in a branch to its the files that have been touched in this commit
	 * @param branch
	 * @return map like "this is a commit message" -> {class1,class2}, "this is a second commit message" -> {class1}
	 * @throws IOException
	 * @throws NoHeadException
	 * @throws GitAPIException
	 */
	HashMap<String, List<String>> getCommitMessagesAndTouchedFilesForEachRevision(String branch) throws IOException, NoHeadException, GitAPIException;
	
	/**
	 * 
	 * @return the current repository that is checked out
	 */
	public Repository getRepository();
	
	/**
	 * 
	 * @return the head branch of all configured branches
	 * @throws NoSuchBranchException
	 */
	public String getHeadBranch() throws NoSuchBranchException;
	
	/**
	 * 
	 * @return a list of configured versions in the mind.properties under git.versionTags
	 */
	public List<String> getConfiguredVersions();
	
	
}
