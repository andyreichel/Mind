package mind;

import interfaces.RCallerApi;
import interfaces.StatisticGenerator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.Test;

import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.RankCouldNotBeCalculatedException;

public class RCallerApiTest {
	@Test
	public void test_getCoefficient_success() throws ConfigurationException, LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException
	{
		Double[] defectInjFrequency = new Double[]{1.0,2.0,3.0,2.2};
		Double[] violationDensity = new Double[]{0.4,1.4,3.0,4.6};
		
		Double expectedRank = 0.8;
		
		MindConfigurationImpl config = new MindConfigurationImpl();
		RCallerApi rcaller = new RCallerApiImpl(config);
		Assert.assertEquals(expectedRank, rcaller.getSpearmanCoefficient(defectInjFrequency, violationDensity), 0.0000001);
	}
	
	@Test(expected=RankCouldNotBeCalculatedException.class)
	public void test_getCoefficient_everyValueZero() throws ConfigurationException, LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException
	{
		Double[] defectInjFrequency = new Double[]{0.0,0.0,0.0,0.0};
		Double[] violationDensity = new Double[]{0.0,0.0,0.0,0.0};
		
		Double expectedRank = 0.8;
		
		MindConfigurationImpl config = new MindConfigurationImpl();
		RCallerApi rcaller = new RCallerApiImpl(config);
		Assert.assertEquals(expectedRank, rcaller.getSpearmanCoefficient(defectInjFrequency, violationDensity), 0.0000001);
	}
	
	@Test(expected=LenghtOfDoubleArraysDifferException.class)
	public void test_getCoefficient_lengthOfArraysDiffer() throws ConfigurationException, LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException
	{
		Double[] defectInjFrequency = new Double[]{1.0,2.0,3.0};
		Double[] violationDensity = new Double[]{0.4,1.4,3.0,4.6};
		
		Double expectedRank = 0.8;
		
		MindConfigurationImpl config = new MindConfigurationImpl();
		RCallerApi rcaller = new RCallerApiImpl(config);
		Assert.assertEquals(expectedRank, rcaller.getSpearmanCoefficient(defectInjFrequency, violationDensity), 0.0000001);
	}
}