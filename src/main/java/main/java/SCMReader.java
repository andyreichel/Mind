package main.java;

import java.util.Set;

public interface SCMReader {
	public int getSizeOfClass(String version, String className);
	public int getNumberOfDefectsRelatedToClass(String version, String className, IssueTrackerReader itReader);
	public int getNumberOfLOCtouched(String version, String className);
}
