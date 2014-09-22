package mind;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;

public class BranchComparerImpl implements BranchComparer {
	public HashMap<String, Integer> getMapWithNumberOfChangesPerResource(
			String branchName1, String branchName2) throws IOException{
		Repository repo = ShowBranchDiff.openRepository();
		OutputStream out = new ByteArrayOutputStream();
		DiffFormatter formatter= new DiffFormatter(out);
		formatter.setRepository(repo);
		formatter.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
		
		AbstractTreeIterator oldTreeParser = ShowBranchDiff.prepareTreeParser(repo,
		"refs/remotes/origin/" + branchName1);
AbstractTreeIterator newTreeParser = ShowBranchDiff.prepareTreeParser(repo,
		"refs/remotes/origin/" +  branchName2);
		formatter.setContext(0);
		List<DiffEntry> diffs = formatter.scan(oldTreeParser, newTreeParser);
		formatter.format(diffs);
		
		return DiffParser.getMapOfChangesPerResourceFromDiffOutput(out.toString());
	}
}
