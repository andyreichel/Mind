package main.java;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Analyzer {
	public static void main(String[] args)
	{
		System.out.println("hey");
	}
	
	public HashMap<String, Integer> getTechnicalDebtRowForRevision(String version, String className, IssueTrackerReader itReader, SCMReader scmReader, SonarReader sonarReader)
	{
		HashMap<String, Integer> technicalDebtRow = new HashMap<String, Integer>();
		HashMap<String, Integer> map = sonarReader.getNumberOfViolationsPerRule(version, className);
		Iterator it = map.entrySet().iterator();
		
		while(it.hasNext())
		{
			Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>) it.next();
			technicalDebtRow.put(pairs.getKey(), pairs.getValue());
			it.remove();
		}
		
		technicalDebtRow.put("locTouched", scmReader.getNumberOfLOCtouched(version, className));
		technicalDebtRow.put("size", scmReader.getSizeOfClass(version, className));
		
		
		technicalDebtRow.put("numberDefects", scmReader.getNumberOfDefectsRelatedToClass(version, className, itReader));
		return technicalDebtRow;
	}
}
