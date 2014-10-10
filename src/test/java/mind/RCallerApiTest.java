package mind;

import interfaces.RCallerApi;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;

import exceptions.AverageCouldNotBeCalculatedException;
import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.PValueCouldNotBeCalculatedException;
import exceptions.RankCouldNotBeCalculatedException;

public class RCallerApiTest {
	@Test
	public void test_getSpearmanCoefficient_success() throws ConfigurationException, LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException
	{
		Double[] defectInjFrequency = new Double[]{1.0,2.0,3.0,2.2};
		Double[] violationDensity = new Double[]{0.4,1.4,3.0,4.6};
		
		Double expectedRank = 0.8;
		
		MindConfigurationImpl config = new MindConfigurationImpl();
		RCallerApi rcaller = new RCallerApiImpl(config);
		Assert.assertEquals(expectedRank, rcaller.getSpearmanCoefficient(defectInjFrequency, violationDensity), 0.0000001);
	}
	
	@Test(expected=RankCouldNotBeCalculatedException.class)
	public void test_getSpearmanCoefficient_everyValueZero() throws ConfigurationException, LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException
	{
		Double[] defectInjFrequency = new Double[]{0.0,0.0,0.0,0.0};
		Double[] violationDensity = new Double[]{0.0,0.0,0.0,0.0};
		
		Double expectedRank = 0.8;
		
		MindConfigurationImpl config = new MindConfigurationImpl();
		RCallerApi rcaller = new RCallerApiImpl(config);
		Assert.assertEquals(expectedRank, rcaller.getSpearmanCoefficient(defectInjFrequency, violationDensity), 0.0000001);
	}
	
	@Test(expected=LenghtOfDoubleArraysDifferException.class)
	public void test_getSpearmanCoefficient_lengthOfArraysDiffer() throws ConfigurationException, LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException
	{
		Double[] defectInjFrequency = new Double[]{1.0,2.0,3.0};
		Double[] violationDensity = new Double[]{0.4,1.4,3.0,4.6};
		
		Double expectedRank = 0.8;
		
		MindConfigurationImpl config = new MindConfigurationImpl();
		RCallerApi rcaller = new RCallerApiImpl(config);
		Assert.assertEquals(expectedRank, rcaller.getSpearmanCoefficient(defectInjFrequency, violationDensity), 0.0000001);
	}
	
	@Test
	public void test_getMeanOfVector_success() throws ConfigurationException, AverageCouldNotBeCalculatedException
	{
		Double[] vector = new Double[]{1.0,1.0,1.0};
		MindConfigurationImpl config = new MindConfigurationImpl();
		RCallerApi rcaller = new RCallerApiImpl(config);
		
		Double expected = 1.0;
		Assert.assertEquals(expected, rcaller.getMeanOfVector(vector));
	}
	
	@Test(expected=AverageCouldNotBeCalculatedException.class)
	public void test_getMeanOfVector_nullInVec() throws ConfigurationException, AverageCouldNotBeCalculatedException
	{
		Double[] vector = new Double[]{5.0/0.0,0.0,0.0};
		MindConfigurationImpl config = new MindConfigurationImpl();
		RCallerApi rcaller = new RCallerApiImpl(config);
		
		rcaller.getMeanOfVector(vector);
	}
	
	@Test
	public void test_getPvalueOfDistribution_differentLenghtOfCols() throws ConfigurationException, PValueCouldNotBeCalculatedException
	{
		double[][] doubleArray= new double[][] {new double[]{1.0,0.0,0.0}, new double[]{1.0,0.0}};
		
		
		RCallerApi rcaller = new RCallerApiImpl(new MindConfigurationImpl());
		Double expected = 0.7388;
		Assert.assertEquals(expected, rcaller.getPvalue(doubleArray),0.0001);
	}
	
	@Test
	public void test_getPvalueOfDistribution_differentLenghtOfColsWithEmptyCols() throws ConfigurationException, PValueCouldNotBeCalculatedException
	{
		double[][] doubleArray= new double[][] {new double[]{1.0,0.0,0.0}, new double[]{1.0,0.0}, new double[]{}};
		
		
		RCallerApi rcaller = new RCallerApiImpl(new MindConfigurationImpl());
		Double expected = 0.7388;
		Assert.assertEquals(expected, rcaller.getPvalue(doubleArray),0.0001);
	}
	
	@Test
	public void test_getPvalueOfDistribution_success() throws ConfigurationException, PValueCouldNotBeCalculatedException
	{
		double[][] doubleArray= new double[][] {new double[]{1.0,0.0,0.0}, new double[]{1.0,0.0,0.0}};
		
		
		RCallerApi rcaller = new RCallerApiImpl(new MindConfigurationImpl());
		Double expected = 1.0;
		Assert.assertEquals(expected, rcaller.getPvalue(doubleArray));
	}
	
	@Test
	public void test_getPvalueOfDistribution_NaN() throws ConfigurationException, PValueCouldNotBeCalculatedException
	{
		double[][] doubleArray= new double[][] {new double[]{0.0,0.0,0.0}, new double[]{0.0,0.0,0.0}};
		
		
		RCallerApi rcaller = new RCallerApiImpl(new MindConfigurationImpl());
		Double expected = Double.NaN;
		Assert.assertEquals(expected, rcaller.getPvalue(doubleArray));
	}
	
}