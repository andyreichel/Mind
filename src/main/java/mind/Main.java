package mind;

import interfaces.StatisticGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import view.HTMLBuilder;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.taskadapter.redmineapi.RedmineException;

import dao.StatisticsDAO;
import dao.TableDAO;
import exceptions.ConfiguredVersionNotExistInSonarException;
import exceptions.KeyNotFoundException;
import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.NoTableSetForCalculatingStatsException;
import exceptions.PValueCouldNotBeCalculatedException;
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
			GitAPIException, RedmineException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, VersionIdentifierConflictException, KeyNotFoundException, LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException, NoTableSetForCalculatingStatsException, PValueCouldNotBeCalculatedException{
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
		 StatisticsDAO stats = statisticGenerator.generateStatistcs(table);
		 
         
         try {
                 File output = new File("c:\\temp\\test.html");
                 PrintWriter out = new PrintWriter(new FileOutputStream(output));
                 out.println(HTMLBuilder.getHtmlPage(table, stats));
                 out.close();
         } catch (FileNotFoundException e) {
                 e.printStackTrace();
         }

	}
}
