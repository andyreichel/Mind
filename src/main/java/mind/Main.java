package mind;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import com.taskadapter.redmineapi.RedmineException;

public class Main {
	public static void main(String[] args) throws ConfigurationException, InvalidRemoteException, TransportException, IOException, GitAPIException, RedmineException
	{
		Configuration config = new PropertiesConfiguration("mind.properties");
		IssueTrackerReader it = new RedmineReader(new RedmineApiImpl(config));

//		SonarWebApi api = new SonarWebApiImpl(config); 
//		GitConnection gitConnection = new GitConnection(config);
//		
//		String[] mainBranches = config.getString("git.mainbranches").split(";");
//		for(String branch : mainBranches)
//			gitConnection.cloneBranch(branch);
//
//
//		
//		Git git = new Git(gitConnection.getRepository());
//		RevWalk walk = new RevWalk(gitConnection.getRepository());
//		RevCommit commit = null;
//
//		Iterable<RevCommit> logs = git.log().call();
//		Iterator<RevCommit> i = logs.iterator();
//
//		while (i.hasNext()) {
//		    commit = walk.parseCommit( i.next() );
//		    System.out.println("+++++++++++++++++++");
//		    System.out.println( commit.getFullMessage() );
//		    System.out.println("-------------------");
//		}
//		
		
//		SCMReader scmReader = new GitReader(git, new BranchComparerImpl(git));
//		Analyzer ana = new Analyzer(new SonarReaderImpl(api), api, null, scmReader);
//		ana.getTechnicalDebtTable();
	}
	
	
}
