package mind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.Version;

@RunWith(MockitoJUnitRunner.class)
public class TestRedmineReader {
	@Mock
	RedmineApi redmineApi;
	
	@Test
	public void getListOfBugIdsForEveryBranch_threeBugs() throws RedmineException
	{
		Issue bug1 = new Issue();
		Tracker bugTracker = new Tracker(1, "Bug");
		Project project = new Project();
		project.setName("testproject");;
		Version version1 = new Version(project,"1.0");
		bug1.setTargetVersion(version1);
		bug1.setId(10000);
		bug1.setTracker(bugTracker);
		Issue bug2 = new Issue();
		bug2.setId(10001);
		bug2.setTargetVersion(version1);
		bug2.setTracker(bugTracker);
		Issue bug3 = new Issue();
		bug3.setId(10002);
		bug3.setTargetVersion(version1);
		bug3.setTracker(bugTracker);
		
		
		List<Issue> issueList = new ArrayList<Issue>();
		issueList.add(bug1);
		issueList.add(bug2);
		issueList.add(bug3);
		
		Mockito.doReturn(issueList).when(redmineApi).getAllIssues();
		Mockito.doReturn("Bug").when(redmineApi).getBugKey();
		
		HashMap<Integer, String> expectedMapOfBugsRelatedToTheirVersion = new HashMap<Integer, String>();

		expectedMapOfBugsRelatedToTheirVersion.put(10000, "1.0");
		expectedMapOfBugsRelatedToTheirVersion.put(10001, "1.0");
		expectedMapOfBugsRelatedToTheirVersion.put(10002, "1.0");
		
		RedmineReader redmineReader = new RedmineReader(redmineApi);
		Assert.assertEquals(expectedMapOfBugsRelatedToTheirVersion, redmineReader.getMapOfBugsRelatedToTheirVersion());
	}
	
	@Test
	public void getListOfBugIdsForEveryBranch_oneBug_twoNormalIssues() throws RedmineException
	{
		Issue bug1 = new Issue();
		Tracker bugTracker = new Tracker(1, "Bug");
		Tracker issueTracker = new Tracker(2, "Issue");
		Project project = new Project();
		project.setName("testproject");;
		Version version1 = new Version(project,"1.0");
		bug1.setTargetVersion(version1);
		bug1.setId(10000);
		bug1.setTracker(bugTracker);
		Issue bug2 = new Issue();
		bug2.setId(10001);
		bug2.setTargetVersion(version1);
		bug2.setTracker(issueTracker);
		Issue bug3 = new Issue();
		bug3.setId(10002);
		bug3.setTargetVersion(version1);
		bug3.setTracker(issueTracker);
		
		
		List<Issue> issueList = new ArrayList<Issue>();
		issueList.add(bug1);
		issueList.add(bug2);
		issueList.add(bug3);
		
		Mockito.doReturn(issueList).when(redmineApi).getAllIssues();
		Mockito.doReturn("Bug").when(redmineApi).getBugKey();
		
		HashMap<Integer, String> expectedMapOfBugsRelatedToTheirVersion = new HashMap<Integer, String>();

		expectedMapOfBugsRelatedToTheirVersion.put(10000, "1.0");
		
		RedmineReader redmineReader = new RedmineReader(redmineApi);
		Assert.assertEquals(expectedMapOfBugsRelatedToTheirVersion, redmineReader.getMapOfBugsRelatedToTheirVersion());
	}
	
	@Test
	public void getListOfBugIdsForEveryBranch_oneBug_noTargetVersionGiven() throws RedmineException
	{
		Issue bug1 = new Issue();
		Tracker bugTracker = new Tracker(1, "Bug");
		Tracker issueTracker = new Tracker(2, "Issue");
		Project project = new Project();
		project.setName("testproject");;
		bug1.setId(10000);
		bug1.setTracker(bugTracker);
		Issue bug2 = new Issue();
		bug2.setId(10001);
		bug2.setTracker(issueTracker);
		Issue bug3 = new Issue();
		bug3.setId(10002);
		bug3.setTracker(issueTracker);
		
		
		List<Issue> issueList = new ArrayList<Issue>();
		issueList.add(bug1);
		issueList.add(bug2);
		issueList.add(bug3);
		
		Mockito.doReturn(issueList).when(redmineApi).getAllIssues();
		Mockito.doReturn("Bug").when(redmineApi).getBugKey();
		
		RedmineReader redmineReader = new RedmineReader(redmineApi);
		Assert.assertEquals(0, redmineReader.getMapOfBugsRelatedToTheirVersion().size());
	}
}
