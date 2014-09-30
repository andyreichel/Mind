package mind;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import com.taskadapter.redmineapi.RedmineException;

public class Main {
	public static void main(String[] args) throws ConfigurationException, InvalidRemoteException, TransportException, IOException, GitAPIException, RedmineException, VersionIdentifierConflictException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, KeyNotFoundException
	{
		Configuration config = new PropertiesConfiguration("mind.properties");
		SonarRunnerApi sonarRunner = new SonarRunnerApiImpl(config);
//		
//		
		GitApi api = new GitApiImpl(config);
		api.cloneBranch("1.0-stable");
		api.cloneBranch("1.1-stable");
		api.cloneBranch("1.2-stable");
		api.cloneBranch("1.3-stable");
		SonarWebApi sonar = new SonarWebApiImpl(config);
		SonarReader sonarReader = new SonarReaderImpl(sonar);
		IssueTrackerReader issueTrackerReader = new RedmineReader(new RedmineApiImpl(config));
		BranchComparer bc = new BranchComparerImpl(api);
		SCMReader scmReader = new GitReader(api, bc);
		Analyzer ana = new Analyzer(sonarReader, issueTrackerReader, scmReader, sonarRunner);
		HashMap<String, HashMap<String, Integer>> table =  ana.getTechnicalDebtTable();
		for(String resource : table.keySet())
		{
			HashMap<String, Integer> entries = table.get(resource);
			System.out.print(resource + "\t\t");
			for(Map.Entry<String, Integer> entry : entries.entrySet())
			{
				System.out.print(entry.getKey() + ": " + entry.getValue() + "\t\t\t");
			}
			System.out.println("++++++++++++++++");
		}
	}
	
	
}
