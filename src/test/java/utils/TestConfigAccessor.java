package utils;


import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.Test;

import utils.ConfigAccessor;

public class TestConfigAccessor {
	@Test
	public void getValueTest_successfull() throws ConfigurationException
	{
		Configuration config =  new PropertiesConfiguration();
		config.setProperty("a", "b");
		
		Assert.assertEquals("b", ConfigAccessor.getValue(config, "a"));
	}
	
	@Test(expected=ConfigurationException.class)
	public void getValueTest_valueNotThere() throws ConfigurationException
	{
		Configuration config =  new PropertiesConfiguration();
		
		ConfigAccessor.getValue(config, "a");
	}
}
