package mind;

import java.io.IOException;

public interface SCMReader {
	public int getSizeOfClass(String version, String className) throws IOException;
	public int getNumberOfDefectsRelatedToClass(String version, String className, IssueTrackerReader itReader);
	public int getNumberOfLOCtouched(String version, String className);
}
