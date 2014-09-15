package test.java;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import main.java.Analyzer;
import main.java.IssueTrackerReader;
import main.java.SCMReader;
import main.java.SonarReader;

import org.junit.Assert;
import org.junit.Test;

public class TestAnalyzer {

	@Test
	public void getTechnicalDebtRowForRevisionTest()
	{
		SCMReader scmReader = new SCMReader(){ 
			public int getSizeOfClass(String version, String className) { return 100; }

			public int getNumberOfLOCtouched(String version, String className) {
				return 50;
			}
			public int getNumberOfDefectsRelatedToClass(String version,
					String className, IssueTrackerReader itReader) {
				return 2;
			}};
			
		IssueTrackerReader itReader = new IssueTrackerReader() {
			public boolean isIssueABug(Integer issueId) {
				return true;
			}};
		
		SonarReader sonarReader = new SonarReader() {
			
			public HashMap<String, Integer> getNumberOfViolationsPerRule(
					String version, String className) {
				
				HashMap<String, Integer> hmap = new HashMap<String, Integer>();
				hmap.put("r1", 1);
				hmap.put("r2", 0);
				return hmap;
			}
		};
			
			Analyzer ana = new Analyzer();
			HashMap<String, Integer> analyzedRow = ana.getTechnicalDebtRowForRevision("1", "someClass", itReader, scmReader, sonarReader);
			
			HashMap<String,Integer> expectedRow = new HashMap<String, Integer>();
			expectedRow.put("r1", 1);
			expectedRow.put("r2", 0);
			expectedRow.put("size", 100);
			expectedRow.put("numberDefects", 2);
			expectedRow.put("locTouched", 50);
			
			Assert.assertEquals(expectedRow, analyzedRow);
	}
	
	
}
