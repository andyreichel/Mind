package mind;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

public class Analyzer {
	private SonarReader sonarReader;
	private SonarWebApi api;
	private IssueTrackerReader issueTrackerReader;
	private SCMReader scmReader;

	public static void main(String[] args) {
		

	}

	public Analyzer(SonarReader sonarReader, SonarWebApi api,
			IssueTrackerReader issueTrackerReader, SCMReader scmReader) {
		this.sonarReader = sonarReader;
		this.api = api;
		this.issueTrackerReader = issueTrackerReader;
		this.scmReader = scmReader;
	}

	public HashMap<String, HashMap<String, Integer>> getTechnicalDebtTable()
			throws ConfigurationException, IOException, InvalidRemoteException,
			TransportException, GitAPIException {
		List<String> resources = api.getListOfAllResources();
		List<AbstractMap.SimpleEntry<String, String>> versionMap = api.getMapOfAllVersionsOfProject();
		HashMap<String, HashMap<String, Integer>> table = new HashMap<String, HashMap<String, Integer>>();
		
		for (String resource : resources) {
				for(int i = versionMap.size()-1; i > 0 ; i--)
				{
					int currentVersionId = i;
					int previousVersionId = i-1;
					table.put(
					resource + "_" + versionMap.get(currentVersionId).getKey(),
					getTechnicalDebtRowForRevision(versionMap.get(currentVersionId), versionMap.get(previousVersionId), resource));
				}
			}
		return table;
	}

	public HashMap<String, Integer> getTechnicalDebtRowForRevision(
			Entry<String, String> currentVersion, Entry<String, String> previousVersion, String className) throws IOException {
		HashMap<String, Integer> technicalDebtRow = new HashMap<String, Integer>();
		HashMap<String, Integer> map = sonarReader
				.getNumberOfViolationsPerRule(currentVersion.getValue(), className);
		Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>) it
					.next();
			technicalDebtRow.put(pairs.getKey(), pairs.getValue());
			it.remove();
		}
		
		technicalDebtRow.put("locTouched",
				scmReader.getNumberOfLOCtouched(currentVersion.getValue(), previousVersion.getValue(), className));
		technicalDebtRow.put("size",
				sonarReader.getSizeOfClass(currentVersion.getValue(), className));

		technicalDebtRow.put("numberDefects", scmReader
				.getNumberOfDefectsRelatedToClass(currentVersion.getKey(), className,
						issueTrackerReader));
		return technicalDebtRow;
	}
}
