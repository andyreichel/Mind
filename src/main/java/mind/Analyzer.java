package mind;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;

public class Analyzer {
	private SonarReader sonarReader;
	private SonarWebApi api;
	private IssueTrackerReader issueTrackerReader;
	private SCMReader scmReader;
	
	public static void main(String[] args) throws ConfigurationException, IOException, InvalidRemoteException, TransportException, GitAPIException, ConfigInvalidException
	{
		Repository repository = ShowBranchDiff.openRepository();
		System.out.println(repository.getConfig());
		StoredConfig bla = repository.getConfig();
		bla.load();
		bla.setString("diff", null, "external", "\"C:/Users/TechDebt/workspacenew/Mind/mydiff.sh\"");
		bla.save();
		System.out.println(bla.getSections().toString());
		System.out.println(bla.getNames("diff").toString());
		System.out.println(bla.getSubsections("diff").toString());
		System.out.println(bla.getString("diff", null, "external"));//("diff", "", "external"));
		//bla.setString("diff", "", name, value);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DiffFormatter df = new DiffFormatter(out);
		
		df.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
		
		df.setRepository(repository);
		// the diff works on TreeIterators, we prepare two for the two branches
		AbstractTreeIterator oldTreeParser = ShowBranchDiff.prepareTreeParser(repository,
				"refs/remotes/origin/master");
		AbstractTreeIterator newTreeParser = ShowBranchDiff.prepareTreeParser(repository,
				"refs/remotes/origin/V2");
		
		// then the procelain diff-command returns a list of diff entries
		Git myGit = new Git(repository);
		myGit.getRepository().getConfig().setString("diff", null, "external", "\"C:/Users/TechDebt/workspacenew/Mind/mydiff.sh\"");
		myGit.getRepository().getConfig().save();
		
		
		List<DiffEntry> diff = myGit.diff()
				.setOldTree(oldTreeParser).setNewTree(newTreeParser).call();
		
		ArrayList<String> diffText = new ArrayList<String>();
		for (DiffEntry entry : diff) {
			df.format(entry);
			RawText r = new RawText(out.toByteArray());
			r.getLineDelimiter();
			diffText.add(out.toString());
			System.out.println(out.toString());
			out.reset();
			System.out.println("Entry: " + entry);
		}
		repository.close();
	}
	
	public Analyzer(SonarReader sonarReader, SonarWebApi api, IssueTrackerReader issueTrackerReader, SCMReader scmReader)
	{
		this.sonarReader = sonarReader;
		this.api = api;
		this.issueTrackerReader = issueTrackerReader;
		this.scmReader = scmReader;
	}
	
	public HashMap<String, HashMap<String, Integer>> getTechnicalDebtTable() throws ConfigurationException, IOException, InvalidRemoteException, TransportException, GitAPIException
	{
		List<String> resources = api.getListOfAllResources();
		HashMap<String, String> versionMap = api.getMapOfAllVersionsOfProject();
		HashMap<String, HashMap<String, Integer>> table = new HashMap<String, HashMap<String,Integer>>();
		for(String resource : resources)
		{
			for(Entry<String, String> version : versionMap.entrySet())
			{
				table.put(resource + "_" + version.getKey(), getTechnicalDebtRowForRevision(version.getValue(), resource));
			}
		}
		return table;
	}
	
	public HashMap<String, Integer> getTechnicalDebtRowForRevision(String versionDate, String className) throws IOException
	{
		HashMap<String, Integer> technicalDebtRow = new HashMap<String, Integer>();
		HashMap<String, Integer> map = sonarReader.getNumberOfViolationsPerRule(versionDate, className);
		Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
		
		while(it.hasNext())
		{
			Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>) it.next();
			technicalDebtRow.put(pairs.getKey(), pairs.getValue());
			it.remove();
		}
		
		technicalDebtRow.put("locTouched", scmReader.getNumberOfLOCtouched(versionDate, className));
		technicalDebtRow.put("size", sonarReader.getSizeOfClass(versionDate, className));
		
		
		technicalDebtRow.put("numberDefects", scmReader.getNumberOfDefectsRelatedToClass(versionDate, className, issueTrackerReader));
		return technicalDebtRow;
	}
}
