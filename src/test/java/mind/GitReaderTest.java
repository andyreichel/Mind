package mind;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import externalinterfaces.BranchComparer;
import externalinterfaces.IssueTrackerReader;

@RunWith(MockitoJUnitRunner.class)
public class GitReaderTest {
	@Mock
	BranchComparer branchComparer;
	
	@Mock
	IssueTrackerReader issueTrackerReader;
	
	@Mock
	GitApiImpl gitConnection;
	
	@Test
	public void test_getNumberOfLOCtouched_noLineTouched() throws IOException, InvalidRemoteException, TransportException, GitAPIException, ConfigurationException
	{
		HashMap<String, Integer> mapWithNumberOfChangesPerResource = new HashMap<String, Integer>();
		Mockito.doReturn(mapWithNumberOfChangesPerResource).when(branchComparer).getMapWithNumberOfChangesPerResource("3-6", "3-7");
		Configuration config = new PropertiesConfiguration("mind.properties");
		Mockito.doNothing().when(gitConnection).initGit(config);
		GitReader git = new GitReader(gitConnection, branchComparer);
		
		Assert.assertEquals(0, git.getNumberOfLOCtouched("3-6", "3-7", "class1"));
	}
	
	@Test
	public void test_getNumberOfLOCtouched_2violations() throws IOException, InvalidRemoteException, TransportException, GitAPIException, ConfigurationException
	{
		HashMap<String, Integer> mapWithNumberOfChangesPerResource = new HashMap<String, Integer>();
		mapWithNumberOfChangesPerResource.put("class1", 2);
		Mockito.doReturn(mapWithNumberOfChangesPerResource).when(branchComparer).getMapWithNumberOfChangesPerResource("3-6", "3-7");
		Configuration config = new PropertiesConfiguration("mind.properties");
		Mockito.doNothing().when(gitConnection).initGit(config);
		GitReader git = new GitReader(gitConnection, branchComparer);
		
		Assert.assertEquals(2, git.getNumberOfLOCtouched("3-6", "3-7", "Project:class1"));
	}
	
	@Test
	public void test_getNumberOfLOCtouched_resourceNotFound() throws IOException, InvalidRemoteException, TransportException, GitAPIException, ConfigurationException
	{
		HashMap<String, Integer> mapWithNumberOfChangesPerResource = new HashMap<String, Integer>();
		mapWithNumberOfChangesPerResource.put("blabla", 2);
		Mockito.doReturn(mapWithNumberOfChangesPerResource).when(branchComparer).getMapWithNumberOfChangesPerResource("3-6", "3-7");
		Configuration config = new PropertiesConfiguration("mind.properties");
		Mockito.doNothing().when(gitConnection).initGit(config);
		GitReader git = new GitReader(gitConnection, branchComparer);
		
		Assert.assertEquals(0, git.getNumberOfLOCtouched("3-6", "3-7", "class1"));
	}
	
	
}
