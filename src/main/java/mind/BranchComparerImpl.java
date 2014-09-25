package mind;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

public class BranchComparerImpl implements BranchComparer {
	GitApiImpl gitConnection;
	
	public BranchComparerImpl(GitApiImpl gitConnection)
	{
		this.gitConnection = gitConnection;
	}
	
	public HashMap<String, Integer> getMapWithNumberOfChangesPerResource(
			String branchName1, String branchName2) throws IOException{
		OutputStream out = new ByteArrayOutputStream();
		DiffFormatter formatter= new DiffFormatter(out);
		formatter.setRepository(gitConnection.getRepository());
		formatter.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
		AbstractTreeIterator oldTreeParser = prepareTreeParser(gitConnection.getRepository(),
		"refs/remotes/origin/TYPO3_3-6");
AbstractTreeIterator newTreeParser = prepareTreeParser(gitConnection.getRepository(),
		"refs/remotes/origin/TYPO3_3-7");
		formatter.setContext(0);
		List<DiffEntry> diffs = formatter.scan(oldTreeParser, newTreeParser);
		formatter.format(diffs);
		HashMap<String, Integer> mapOfChangesPerResource = DiffParser.getMapOfChangesPerResourceFromDiffOutput(out.toString());
		return mapOfChangesPerResource;
	}
	
	 public static AbstractTreeIterator prepareTreeParser(
				Repository repository, String ref) throws IOException,
				MissingObjectException, IncorrectObjectTypeException {
			// from the commit we can build the tree which allows us to construct
			// the TreeParser
			Ref head = repository.getRef(ref);
			RevWalk walk = new RevWalk(repository);
			RevCommit commit = walk.parseCommit(head.getObjectId());
			RevTree tree = walk.parseTree(commit.getTree().getId());
			CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
			ObjectReader oldReader = repository.newObjectReader();
			try {
				oldTreeParser.reset(oldReader, tree.getId());
			} finally {
				oldReader.release();
			}
			walk.dispose();
			return oldTreeParser;
		}
}
