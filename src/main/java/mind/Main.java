package mind;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

public class Main {
	public static void main(String[] args) throws ConfigurationException, InvalidRemoteException, TransportException, IOException, GitAPIException
	{
		Configuration config = new PropertiesConfiguration("mind.properties");
		SonarWebApi api = new SonarWebApiImpl(config); 
		SCMReader scmReader = new GitReader(config, new BranchComparerImpl());
		Analyzer ana = new Analyzer(new SonarReaderImpl(api), api, null, scmReader);
		ana.getTechnicalDebtTable();
	}
	
	
}
