package utils;


import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.Test;

import utils.ConfigAccessor;

public class ConfigAccessorTest {
	@Test
	public void test_getValue_successfull() throws ConfigurationException
	{
		Configuration config =  new PropertiesConfiguration();
		config.setProperty("a", "b");
		
		Assert.assertEquals("b", ConfigAccessor.getValue(config, "a"));
	}
	
	@Test(expected=ConfigurationException.class)
	public void test_getValue_valueNotThere() throws ConfigurationException
	{
		Configuration config =  new PropertiesConfiguration();
		
		ConfigAccessor.getValue(config, "a");
	}
}
