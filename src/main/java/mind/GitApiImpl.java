package mind;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.internal.storage.file.WindowCache;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.storage.file.WindowCacheConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class GitApiImpl implements GitApi {
	private Repository repo; 
	private CredentialsProvider cp;
	private String gitUrl;
	private String workingDir;
	List<String> configuredVersions;
	
	public GitApiImpl(Configuration config) throws IOException, InvalidRemoteException, TransportException, GitAPIException
	{
		initGit(config);
	}
	
	protected void initGit(Configuration config) throws IOException, InvalidRemoteException, TransportException, GitAPIException
	{
		//FIXME: COMMENT ME 
		WindowCacheConfig wcc = new WindowCacheConfig();
		
		wcc.setPackedGitLimit(Integer.MAX_VALUE);
		wcc.setStreamFileThreshold(Integer.MAX_VALUE);
		wcc.setDeltaBaseCacheLimit(Integer.MAX_VALUE);
		wcc.install();
		
		String gitName = config.getString("git.name");
		String gitPw = config.getString("git.password");
		gitUrl = config.getString("git.url");
		workingDir = config.getString("git.workingdir");
		configuredVersions = Arrays.asList(config.getString("git.versiontags").split(";"));
		

		cp = new UsernamePasswordCredentialsProvider(gitName, gitPw);

		File localPath = File.createTempFile("TestGitRepository", "");
		localPath.delete();


	}
	
	public void cloneBranch(String branch) throws IOException, InvalidRemoteException, TransportException, GitAPIException
	{
		File branchDir = new File(workingDir + "\\" + branch);
		if(branchDir.exists())
		{
			if(repo == null)
				setRepository(branch);
			return;
		}	
		
		CloneCommand cc = new CloneCommand().setCredentialsProvider(cp)
		.setDirectory(branchDir).setURI(gitUrl).setBranch(branch);
		
		cc.call();
		setRepository(branch);
	}
	
	
	public void setRepository(String branch) throws IOException
	{
		File branchDir = new File(workingDir + "\\" + branch);
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repo = builder
		.setGitDir(new File(branchDir.getAbsolutePath() + "/.git"))
		.readEnvironment().findGitDir().build();
	}
	
	public HashMap<String, List<String>> getCommitMessagesAndTouchedFilesForEachRevision(String branch) throws IOException, NoHeadException, GitAPIException
	{
		File branchDir = new File(workingDir + "\\" + branch);
		if(branchDir.exists())
		{
			if(repo == null)
				setRepository(branch);
		}else throw new NoSuchBranchException("Branch " + branch + " does not exit.");	
		
		 RevWalk walk = new RevWalk(repo);
		 RevCommit commit = null;
		 
		 Git git = new Git(repo);
		 Iterable<RevCommit> logs = git.log().call();
		 Iterator<RevCommit> i = logs.iterator();
	
		 HashMap<String, List<String>> commitMessagesAndFiles = new HashMap<String,List<String>>();
		 while (i.hasNext()) {
		     commit = walk.parseCommit( i.next() );
		     HashMap<String, List<String>> messageAndFiles = new HashMap<String, List<String>>();
		     commitMessagesAndFiles.put(commit.getFullMessage(), getFilesInCommit(repo, commit));
		 }
		 walk.dispose();
		 repo.close();
		 return commitMessagesAndFiles;
	}
		 
    /**
     * Returns the list of files changed in a specified commit. If the
     * repository does not exist or is empty, an empty list is returned.
     *
     * @param repository
     * @param commit
     *            if null, HEAD is assumed.
     * @return list of files changed in a commit
     * @throws Exception 
     */
    public static List<String> getFilesInCommit(Repository repository, RevCommit commit) throws IOException {
            List<String> list = new ArrayList<String>();
            if (!hasCommits(repository)) {
                    return list;
            }
            RevWalk rw = new RevWalk(repository);
            try {
                    if (commit == null) {
                    		ObjectId object = repository.resolve(Constants.HEAD);
                            commit = rw.parseCommit(object);
                    }

                    if (commit.getParentCount() == 0) {
                            TreeWalk tw = new TreeWalk(repository);
                            tw.reset();
                            tw.setRecursive(true);
                            tw.addTree(commit.getTree());
                            while (tw.next()) {
                                    list.add("new");
                            }
                            tw.release();
                    } else {
                            RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
                            DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
                            df.setRepository(repository);
                            df.setDiffComparator(RawTextComparator.DEFAULT);
                            df.setDetectRenames(true);
                            List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());
                            for (DiffEntry diff : diffs) {
                                    String objectId = diff.getNewId().name();
                                         list.add(diff.getNewPath());
                            }
                    }
            } catch (Throwable t) {
                    throw new IOException("failed");
            } finally {
                    rw.dispose();
            }
            return list;
    }

    /**
     * Determine if a repository has any commits. This is determined by checking
     * the for loose and packed objects.
     *
     * @param repository
     * @return true if the repository has commits
     */
    public static boolean hasCommits(Repository repository) {
            if (repository != null && repository.getDirectory().exists()) {
                    return (new File(repository.getDirectory(), "objects").list().length > 2)
                                    || (new File(repository.getDirectory(), "objects/pack").list().length > 0);
            }
            return false;
    }

	
	public Repository getRepository()
	{
		return repo;
	}

	public String getHeadBranch() throws NoSuchBranchException {
		if(configuredVersions.size() == 0)
		{
			throw new NoSuchBranchException("No branches available");
		}
		return configuredVersions.get(configuredVersions.size()-1);
	}

	public List<String> getConfiguredVersions() {
		// TODO Auto-generated method stub
		return configuredVersions;
	}
}
