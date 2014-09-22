package mind;

import java.io.IOException;


public interface SCMReader {
	public int getNumberOfDefectsRelatedToClass(String version, String className, IssueTrackerReader itReader);
	public int getNumberOfLOCtouched(String currentVersion, String previousVersion, String className) throws IOException;
	public BranchComparer getBranchComparer();
}
