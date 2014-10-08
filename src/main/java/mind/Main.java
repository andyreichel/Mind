package mind;

import interfaces.StatisticGenerator;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import com.google.inject.Guice;
import com.google.inject.Injector;
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
		 
		 Injector injector = Guice.createInjector(new MindInjector());
		 TableWithCodeInfoGenerator tableGenerator = injector.getInstance(TableWithCodeInfoGenerator.class);
		 StatisticGenerator statisticGenerator = injector.getInstance(StatisticGenerator.class);
		 
		 TableDAO table = tableGenerator.getTableWithCodeInfoForEveryClassInEveryRelease();
		 table.filterTable();		 
		 HashMap<String, Double> ranks = statisticGenerator.getSpearmanCoefficientForAllRulesInTable(table);
		 for(String rule: ranks.keySet())
		 {
			 System.out.println("rule: " + rule + " value: " + ranks.get(rule));
			 
		 }
		 
	}
}
