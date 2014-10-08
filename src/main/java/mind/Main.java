package mind;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import externalinterfaces.BranchComparer;
import externalinterfaces.GitApi;
import externalinterfaces.IssueTrackerReader;
import externalinterfaces.SCMReader;
import externalinterfaces.SonarReader;
import externalinterfaces.SonarRunnerApi;
import externalinterfaces.SonarWebApi;
import externalinterfaces.SpearmanCorrelationCoefficient;


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
		 Configuration config = new
		 PropertiesConfiguration("mind.properties");
		 SonarRunnerApi sonarRunner = new SonarRunnerApiImpl(config);
		 GitApi api = new GitApiImpl(config);
		
		 SonarWebApi sonar = new SonarWebApiImpl(config);
		 SonarReader sonarReader = new SonarReaderImpl(sonar);
		 IssueTrackerReader issueTrackerReader = new RedmineReader(new
		 RedmineApiImpl(config));
		 BranchComparer bc = new BranchComparerImpl(api);
		 SCMReader scmReader = new GitReader(api, bc);
		 Analyzer ana = new Analyzer(sonarReader, issueTrackerReader,
		 scmReader, sonarRunner);
		 TableDAO table = ana.getTechnicalDebtTable();
		
		 table.filterTable();
		
		 Set<String> allRules = table.getAllRulesInTable();
		 Double[] defectInjectionFrequencyColumn = table.getDefectInjectionFrequencyColumnForRule();
		 List<Double> ranks= new ArrayList<Double>();
		 SpearmanCorrelationCoefficient spearman = new SpearmanCorrelationCoefficientImpl(config);
		 for(String rule : allRules)
		 {
			 Double coeff;
			 try
			 {
				 coeff = spearman.getCoefficient(defectInjectionFrequencyColumn, table.getViolationDensityDencityColumnForRule(rule));	 
			 }catch(RankCouldNotBeCalculatedException re)
			 {
				 coeff = null;
			 }
			 
			 ranks.add(coeff);
		 }
	}
}
