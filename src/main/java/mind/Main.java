package mind;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import com.taskadapter.redmineapi.RedmineException;

public class Main {
	public static void main(String[] args) throws ConfigurationException, InvalidRemoteException, TransportException, IOException, GitAPIException, RedmineException
	{
		Configuration config = new PropertiesConfiguration("mind.properties");
		GitApi api = new GitApiImpl(config);
		SonarWebApi sonar = new SonarWebApiImpl(config);
		List<String> resources = sonar.getListOfAllResources();
		System.out.println(resources.get(0));
		
	//	Analyzer ana = new Analyzer(sonarReader, api, issueTrackerReader, scmReader)
		

	}
	

	
	
}
