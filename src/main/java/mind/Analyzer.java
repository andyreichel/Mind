package mind;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

public class Analyzer {
	public static void main(String[] args) throws ConfigurationException, IOException, InvalidRemoteException, TransportException, GitAPIException
	{
		SCMReader scm = new GitReader(new PropertiesConfiguration("mind.properties"));
		scm.getSizeOfClass("1", "someClass");
	}
	
	public HashMap<String, Integer> getTechnicalDebtRowForRevision(String version, String className, IssueTrackerReader itReader, SCMReader scmReader, SonarReader sonarReader) throws IOException
	{
		HashMap<String, Integer> technicalDebtRow = new HashMap<String, Integer>();
		HashMap<String, Integer> map = sonarReader.getNumberOfViolationsPerRule(version, className);
		Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
		
		while(it.hasNext())
		{
			Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>) it.next();
			technicalDebtRow.put(pairs.getKey(), pairs.getValue());
			it.remove();
		}
		
		technicalDebtRow.put("locTouched", scmReader.getNumberOfLOCtouched(version, className));
		technicalDebtRow.put("size", scmReader.getSizeOfClass(version, className));
		
		
		technicalDebtRow.put("numberDefects", scmReader.getNumberOfDefectsRelatedToClass(version, className, itReader));
		return technicalDebtRow;
	}
}
