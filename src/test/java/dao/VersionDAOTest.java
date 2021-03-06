package dao;

import interfaces.IssueTrackerReader;
import interfaces.SCMReader;
import interfaces.SonarReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import dao.VersionDAO;
import exceptions.ConfiguredVersionNotExistInSonarException;
import exceptions.KeyNotFoundException;
import exceptions.UnequalNumberOfVersionsException;


@RunWith(MockitoJUnitRunner.class)
public class VersionDAOTest {
	@Mock
	SCMReader scmReader;
	
	@Mock
	IssueTrackerReader itReader;
	
	@Mock
	SonarReader sonarReader;
	
	@Test
	public void test_getVersionDao_sameNumberOfVersions( ) throws IOException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, KeyNotFoundException, ConfigurationException
	{
		List<String> scmVersions = new ArrayList<String>();
		scmVersions.add("ascm");
		List<String> itVersions = new ArrayList<String>();
		itVersions.add("ait");
		List<String> sonarVersions = new ArrayList<String>();
		sonarVersions.add("asonar");
		
		Mockito.doReturn(scmVersions).when(scmReader).getConfiguredVersions();
		Mockito.doReturn(itVersions).when(itReader).getConfiguredVersions();
		Mockito.doReturn(sonarVersions).when(sonarReader).getConfiguredVersions();
		
		VersionDAO versionDao = new VersionDAO(scmReader, itReader, sonarReader);
		Assert.assertEquals("ascm", versionDao.getScmVersion("ait"));
		Assert.assertEquals("ait", versionDao.getIssueTrackerVersion("ait"));
		Assert.assertEquals("asonar", versionDao.getSonarKeyVersion("ait"));
	}
	
	@Test(expected=KeyNotFoundException.class)
	public void test_getVersionDao_noVersions( ) throws IOException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, KeyNotFoundException, ConfigurationException
	{
		List<String> scmVersions = new ArrayList<String>();
		List<String> itVersions = new ArrayList<String>();
		List<String> sonarVersions = new ArrayList<String>();
		
		Mockito.doReturn(scmVersions).when(scmReader).getConfiguredVersions();
		Mockito.doReturn(itVersions).when(itReader).getConfiguredVersions();
		Mockito.doReturn(sonarVersions).when(itReader).getConfiguredVersions();
		
		VersionDAO versionDao = new VersionDAO(scmReader, itReader, sonarReader);
		Assert.assertEquals("0", versionDao.getIssueTrackerVersion("a"));
	}
	
	@Test(expected=UnequalNumberOfVersionsException.class)
	public void test_getVersionDao_unequalNumberOfVersions( ) throws IOException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, ConfigurationException
	{
		List<String> scmVersions = new ArrayList<String>();
		scmVersions.add("a");
		List<String> itVersions = new ArrayList<String>();
		List<String> sonarVersions = new ArrayList<String>();
		
		Mockito.doReturn(scmVersions).when(scmReader).getConfiguredVersions();
		Mockito.doReturn(itVersions).when(itReader).getConfiguredVersions();
		Mockito.doReturn(sonarVersions).when(sonarReader).getConfiguredVersions();
		
		new VersionDAO(scmReader, itReader, sonarReader);
	}
}
