package mind;

import java.util.List;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;

public interface RedmineApi {
	List<Issue> getAllIssues() throws RedmineException;
	String getBugKey() throws RedmineException;
}
