package TechnicalDebt.testOne;

import java.util.List;


import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;

public class RedmineReader {
	private final static String BUG_KEY = "Bug"; 
    private static String redmineHost = "https://redmine.fc-md.umd.edu";
    private static String apiAccessKey = "67070486a7599d3999a4282fddd0a8e9bde75ecb";
    private static String projectKey = "MIND";
    private static Integer queryId = null; // any
    
    public static void read()
    {
    	RedmineManager mgr = new RedmineManager(redmineHost,apiAccessKey);
    	try{
    		tryGetIssues(mgr);
    		mgr.shutdown();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    private static void tryGetIssues(RedmineManager mgr) throws Exception {
        List<Issue> issues = mgr.getIssues(projectKey, queryId);
        
        for(Issue i : issues)
        {
        	if(isIssueABug(i))
        	{
        		System.out.println(i.getId());
        	}
        }	
    }
    
    private static boolean isIssueABug(Issue i)
    {
    	return i.getTracker().getName().equals(BUG_KEY);
    }
}

