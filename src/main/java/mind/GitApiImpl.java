package mind;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.internal.storage.file.WindowCache;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.storage.file.WindowCacheConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GitApiImpl implements GitApi {
	private Repository repo; 
	private CredentialsProvider cp;
	private String gitUrl;
	private String workingDir;
	
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
	
	public Repository getRepository()
	{
		return repo;
	}
}
