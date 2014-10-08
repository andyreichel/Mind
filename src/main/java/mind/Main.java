package mind;

import interfaces.BranchComparer;
import interfaces.GitApi;
import interfaces.IssueTrackerReader;
import interfaces.RCallerApi;
import interfaces.SCMReader;
import interfaces.SonarReader;
import interfaces.SonarRunnerApi;
import interfaces.SonarWebApi;
import interfaces.StatisticGenerator;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import com.taskadapter.redmineapi.RedmineException;

import dao.TableDAO;
import exceptions.ConfiguredVersionNotExistInSonarException;
import exceptions.KeyNotFoundException;
import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.RankCouldNotBeCalculatedException;
import exceptions.UnequalNumberOfVersionsException;
import exceptions.VersionIdentifierConflictException;


/**
 * In this class the differnt classes are plugged in together and the rank is generated for all of the rules. 
 *
 */
public class Main {

	public static void main(String[] args) throws ConfigurationException,
			InvalidRemoteException, TransportException, IOException,
			GitAPIException, RedmineException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, VersionIdentifierConflictException, KeyNotFoundException, LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException{
		 PropertyConfigurator.configure(System.getProperty("logProperties"));
		 Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.ERROR);
		 Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.ERROR);
		 Logger.getLogger("org.apache.http").setLevel(Level.ERROR);
		 Logger.getLogger("httpclient").setLevel(Level.ERROR);
		 Configuration config = new	 PropertiesConfiguration("mind.properties");
		 SonarRunnerApi sonarRunner = new SonarRunnerApiImpl(config);
		 GitApi api = new GitApiImpl(config);
		
		 SonarWebApi sonar = new SonarWebApiImpl(config);
		 SonarReader sonarReader = new SonarReaderImpl(sonar);
		 IssueTrackerReader issueTrackerReader = new RedmineReader(new
		 RedmineApiImpl(config));
		 BranchComparer bc = new BranchComparerImpl(api);
		 SCMReader scmReader = new GitReader(api, bc);
		 TableWithCodeInfoGenerator ana = new TableWithCodeInfoGenerator(sonarReader, issueTrackerReader,
		 scmReader, sonarRunner);
		 
		 TableDAO table = ana.getTableWithCodeInfoForEveryClassInEveryRelease();
		 table.filterTable();
		
		 RCallerApi rcaller = new RCallerApiImpl(config); 
		 StatisticGenerator statisticGenerator = new StatisticGeneratorImpl(rcaller);
		 statisticGenerator.getSpearmanCoefficientForAllRulesInTable(table);
		 
	}
}
