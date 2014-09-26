package mind;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import com.taskadapter.redmineapi.RedmineException;

public class Main {
	public static void main(String[] args) throws ConfigurationException, InvalidRemoteException, TransportException, IOException, GitAPIException, RedmineException, VersionIdentifierConflictException, ConfiguredVersionNotExistInSonarException
	{
		Configuration config = new PropertiesConfiguration("mind.properties");
		
		GitApi api = new GitApiImpl(config);
		SonarWebApi sonar = new SonarWebApiImpl(config);
		SonarReader sonarReader = new SonarReaderImpl(sonar);
		IssueTrackerReader issueTrackerReader = new RedmineReader(new RedmineApiImpl(config));
		BranchComparer bc = new BranchComparerImpl(api);
		SCMReader scmReader = new GitReader(api, bc);
		Analyzer ana = new Analyzer(sonarReader, issueTrackerReader, scmReader);
		System.out.println(ana.getTechnicalDebtTable().size());
	}
	

	
	
}
