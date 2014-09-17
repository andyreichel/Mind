package mind;

import java.io.IOException;

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


public class ShowBranchDiff {
//	public static void main(String[] args) throws IOException, GitAPIException {
//		Repository repository = openRepository();
//		// the diff works on TreeIterators, we prepare two for the two branches
//		AbstractTreeIterator oldTreeParser = prepareTreeParser(repository,
//				"refs/heads/V2");
//		AbstractTreeIterator newTreeParser = prepareTreeParser(repository,
//				"refs/heads/master");
//		// then the procelain diff-command returns a list of diff entries
//		List<DiffEntry> diff = new Git(repository).diff()
//				.setOldTree(oldTreeParser).setNewTree(newTreeParser).call();
//		for (DiffEntry entry : diff) {
//			System.out.println("Entry: " + entry);
//		}
//		repository.close();
//	}
	
	
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
	 
	 public static Repository openRepository() throws IOException {
		 FileRepositoryBuilder builder = new FileRepositoryBuilder();
		 Repository repository = builder
		 .readEnvironment() // scan environment GIT_* variables
		 .findGitDir() // scan up the file system tree
		 .build();
		 return repository;
		 }
}