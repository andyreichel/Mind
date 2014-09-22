package mind;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.HistogramDiff;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;

public class Analyzer {
	private SonarReader sonarReader;
	private SonarWebApi api;
	private IssueTrackerReader issueTrackerReader;
	private SCMReader scmReader;

	public static void main(String[] args) throws ConfigurationException,
			IOException, InvalidRemoteException, TransportException,
			GitAPIException, ConfigInvalidException {
		Repository repo = ShowBranchDiff.openRepository();
		repo.getConfig().load();
		OutputStream out = new ByteArrayOutputStream();
		DiffFormatter formatter= new DiffFormatter(out);
		formatter.setRepository(repo);
		formatter.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
		
		AbstractTreeIterator oldTreeParser = ShowBranchDiff.prepareTreeParser(repo,
		"refs/remotes/origin/V2");
AbstractTreeIterator newTreeParser = ShowBranchDiff.prepareTreeParser(repo,
		"refs/remotes/origin/master");
		formatter.setContext(0);
		List<DiffEntry> diffs = formatter.scan(oldTreeParser, newTreeParser);
		//formatter.format(diffs);
		formatter.format(diffs.get(0));
		System.out.println(out.toString());
		//System.out.println(diffs.get(0));
//		for(DiffEntry diff : diffs)
//		{
//			System.out.println(diff.getChangeType());
//			//diff.
//			
//		}
		//System.out.println(getDiff("C:\\Users\\TechDebt\\workspacenew\\Mind\\README", "C:\\Users\\TechDebt\\workspacenew\\MindBranchV2\\README"));
		

	}

	private static String getDiff(String file1, String file2) {
	    OutputStream out = new ByteArrayOutputStream();
	    try {
	        RawText rt1 = new RawText(new File(file1));
	        RawText rt2 = new RawText(new File(file2));
	        EditList diffList = new EditList();
	        diffList.addAll(new HistogramDiff().diff(RawTextComparator.WS_IGNORE_ALL, rt1, rt2));
	        new DiffFormatter(out).format(diffList, rt1, rt2);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return out.toString();
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
				scmReader.getNumberOfLOCtouched(currentVersion.getValue(), previousVersion.getValue(), className, null));
		technicalDebtRow.put("size",
				sonarReader.getSizeOfClass(currentVersion.getValue(), className));

		technicalDebtRow.put("numberDefects", scmReader
				.getNumberOfDefectsRelatedToClass(currentVersion.getKey(), className,
						issueTrackerReader));
		return technicalDebtRow;
	}
}
