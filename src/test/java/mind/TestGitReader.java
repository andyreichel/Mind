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

@RunWith(MockitoJUnitRunner.class)
public class TestGitReader {
	@Mock
	BranchComparer branchComparer;
	
	@Test
	public void getNumberOfLOCtouchedTest_noLineTouched() throws IOException, InvalidRemoteException, TransportException, GitAPIException, ConfigurationException
	{
		HashMap<String, Integer> mapWithNumberOfChangesPerResource = new HashMap<String, Integer>();
		Mockito.doReturn(mapWithNumberOfChangesPerResource).when(branchComparer).getMapWithNumberOfChangesPerResource("3-6", "3-7");
		Configuration config = new PropertiesConfiguration("mind.properties");
		GitReader git = new GitReader(config, branchComparer);
		
		
		
		Assert.assertEquals(0, git.getNumberOfLOCtouched("3-6", "3-7", "class1"));
	}
}
