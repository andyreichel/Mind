package mind;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;

/**
 * Classes that implement this interface provide methods to access a SCM such as Git or SVN
 * 
 *
 */
public interface SCMReader {
	/**
	 * returns the number of lines of code touched comparing a class in two different branches
	 * @param currentVersion
	 * @param previousVersion
	 * @param className
	 * @return
	 * @throws IOException
	 * @throws NoSuchBranchException
	 */
	public int getNumberOfLOCtouched(String currentVersion, String previousVersion, String className) throws IOException, NoSuchBranchException;
	
	/**
	 * 
	 * @return branch comparer thats used to compare two branches
	 */
	public BranchComparer getBranchComparer();
	
	/**
	 * returns a map that relates commit messages of a revision in a branch to its the files that have been touched in this commit
	 * @param branch
	 * @return map like "this is a commit message" -> {class1,class2}, "this is a second commit message" -> {class1}
	 * @throws IOException
	 * @throws NoHeadException
	 * @throws GitAPIException
	 */
	public HashMap<String, List<String>> getCommitMessagesAndTouchedFilesForEachRevision(String branch) throws IOException, NoHeadException, GitAPIException;
	public String getHeadBranch() throws NoSuchBranchException;
	public List<String> getConfiguredVersions();
}
