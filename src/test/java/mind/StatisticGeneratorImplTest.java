package mind;

import interfaces.RCallerApi;
import interfaces.StatisticGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import testutils.TestUtils;

import com.google.common.collect.ImmutableMap;

import dao.ResourceInfoRow;
import dao.TableDAO;
import exceptions.AverageCouldNotBeCalculatedException;
import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.NoTableSetForCalculatingStatsException;
import exceptions.PValueCouldNotBeCalculatedException;
import exceptions.PropertyNotFoundException;
import exceptions.RankCouldNotBeCalculatedException;

@RunWith(MockitoJUnitRunner.class)
public class StatisticGeneratorImplTest {
	@Mock
	RCallerApi rcaller;
	
	@Test
	public void test_getSpearmanCoefficientForAllRulesInTable_success() throws LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException, PropertyNotFoundException, NoTableSetForCalculatingStatsException
	{
		ResourceInfoRow class1v1_row = TestUtils.getResourceInfoRow("class1", 1, 13, 5, ImmutableMap.of("r1",5, "r2", 2, "r3", 0));
		ResourceInfoRow class2v1_row = TestUtils.getResourceInfoRow("class2", 3, 8, 2, ImmutableMap.of("r1",4, "r2", 8, "r3", 0));
		
		ResourceInfoRow class1v2_row = TestUtils.getResourceInfoRow("class1", 1, 1, 3, ImmutableMap.of("r1", 0, "r2", 2, "r3", 0));
		
		
		List<ResourceInfoRow> v1rows = new ArrayList<ResourceInfoRow>();
		v1rows.add(class1v1_row);
		v1rows.add(class2v1_row);
		List<ResourceInfoRow> v2rows = new ArrayList<ResourceInfoRow>();
		v2rows.add(class1v2_row);
		
		LinkedHashMap<String, List<ResourceInfoRow>> tableMap = new LinkedHashMap<String, List<ResourceInfoRow>>();
		tableMap.put("v1", v1rows);
		tableMap.put("v2", v2rows);
		TableDAO table = new TableDAO(tableMap);
		
		
		HashMap<String, Double> expectedRankList = new HashMap<String, Double>();
		expectedRankList.put("r1", 1.4);
		expectedRankList.put("r2", 2.0);
		expectedRankList.put("r3", null);
		
		Double[] defectInj = new Double[]{1.0/13.0, 3.0/8.0, 1.0};
		Double[] r1 = new Double[]{5.0/5.0, 4.0/2.0, 0.0};
		Double[] r2 = new Double[]{2.0/5.0, 8.0/2.0, 2.0/3.0};
		Double[] r3 = new Double[]{0.0, 0.0, 0.0};
		
		Mockito.doReturn(1.4).when(rcaller).getSpearmanCoefficient(defectInj, r1);
		Mockito.doReturn(2.0).when(rcaller).getSpearmanCoefficient(defectInj, r2);
		Mockito.doThrow(RankCouldNotBeCalculatedException.class).when(rcaller).getSpearmanCoefficient(defectInj, r3);
		
		StatisticGenerator stat = new StatisticGeneratorImpl(rcaller);
		stat.setTableDAO(table);
		Assert.assertEquals(expectedRankList, stat.getSpearmanCoefficientForAllRulesInTable());
	}
	
	@Test(expected=NoTableSetForCalculatingStatsException.class)
	public void test_getSpearmanCoefficientForAllRulesInTable_noTableSet() throws LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException, PropertyNotFoundException, NoTableSetForCalculatingStatsException
	{
		StatisticGenerator stat = new StatisticGeneratorImpl(rcaller);
		stat.getSpearmanCoefficientForAllRulesInTable();
	}
	
	@Test
	public void test_getAverageViolationsForAllRulesInTable_success() throws LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException, PropertyNotFoundException, NoTableSetForCalculatingStatsException, AverageCouldNotBeCalculatedException
	{
		ResourceInfoRow class1v1_row = TestUtils.getResourceInfoRow("class1", 1, 1, 1, ImmutableMap.of("r1",1, "r2", 1, "r3", 1));
		ResourceInfoRow class2v1_row = TestUtils.getResourceInfoRow("class2", 1, 1, 1, ImmutableMap.of("r1",1, "r2", 1, "r3", 1));
		
		List<ResourceInfoRow> v1rows = new ArrayList<ResourceInfoRow>();
		v1rows.add(class1v1_row);
		v1rows.add(class2v1_row);
		
		LinkedHashMap<String, List<ResourceInfoRow>> tableMap = new LinkedHashMap<String, List<ResourceInfoRow>>();
		tableMap.put("v1", v1rows);
		
		TableDAO table = new TableDAO(tableMap);
		
		Mockito.when(rcaller.getMeanOfVector(Mockito.any(Double[].class))).thenReturn(1.0).thenReturn(1.0).thenReturn(2.0);
		StatisticGenerator stat = new StatisticGeneratorImpl(rcaller);
		
		HashMap<String, Double> expectedAverageViolationsMap = new HashMap<String, Double>();
		expectedAverageViolationsMap.put("r1", 1.0);
		expectedAverageViolationsMap.put("r2", 1.0);
		expectedAverageViolationsMap.put("r3", 2.0);
		
		stat.setTableDAO(table);
		Assert.assertEquals(expectedAverageViolationsMap, stat.getAverageViolationsForAllRulesInTable());
	}

	
	@Test
	public void test_getAverageViolationsForAllRulesInTable_rThrowsCalculationException() throws LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException, PropertyNotFoundException, NoTableSetForCalculatingStatsException, ConfigurationException, AverageCouldNotBeCalculatedException
	{
		ResourceInfoRow class1v1_row = TestUtils.getResourceInfoRow("class1", 1, 1, 1, ImmutableMap.of("r1",1, "r2", 1, "r3", 1));
		
		List<ResourceInfoRow> v1rows = new ArrayList<ResourceInfoRow>();
		v1rows.add(class1v1_row);
		
		LinkedHashMap<String, List<ResourceInfoRow>> tableMap = new LinkedHashMap<String, List<ResourceInfoRow>>();
		tableMap.put("v1", v1rows);
		
		TableDAO table = new TableDAO(tableMap);
		
		Mockito.when(rcaller.getMeanOfVector(Mockito.any(Double[].class))).thenReturn(1.0).thenThrow(AverageCouldNotBeCalculatedException.class).thenReturn(2.0);
		StatisticGenerator stat = new StatisticGeneratorImpl(rcaller);
		
		HashMap<String, Double> expectedAverageViolationsMap = new HashMap<String, Double>();
		expectedAverageViolationsMap.put("r1", 1.0);
		expectedAverageViolationsMap.put("r2", null);
		expectedAverageViolationsMap.put("r3", 2.0);

		stat.setTableDAO(table);
		Assert.assertEquals(expectedAverageViolationsMap, stat.getAverageViolationsForAllRulesInTable());
	}
	
	@Test
	public void test_getAverageViolationsForAllRulesInTable_ruleHasZeroViolations() throws LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException, PropertyNotFoundException, NoTableSetForCalculatingStatsException, ConfigurationException, AverageCouldNotBeCalculatedException
	{
		ResourceInfoRow class1v1_row = TestUtils.getResourceInfoRow("class1", 1, 1, 1, ImmutableMap.of("r1",0, "r2", 1, "r3", 0));
		ResourceInfoRow class1v2_row = TestUtils.getResourceInfoRow("class1", 1, 1, 1, ImmutableMap.of("r1",4, "r2", 1, "r3", 0));
		ResourceInfoRow class2v1_row = TestUtils.getResourceInfoRow("class2", 1, 1, 1, ImmutableMap.of("r1",4, "r2", 1, "r3", 0));
		
		List<ResourceInfoRow> v1rows = new ArrayList<ResourceInfoRow>();
		v1rows.add(class1v1_row);
		v1rows.add(class2v1_row);
		List<ResourceInfoRow> v2rows = new ArrayList<ResourceInfoRow>();
		v2rows.add(class1v2_row);
		
		LinkedHashMap<String, List<ResourceInfoRow>> tableMap = new LinkedHashMap<String, List<ResourceInfoRow>>();
		tableMap.put("v1", v1rows);
		tableMap.put("v2", v2rows);
		
		TableDAO table = new TableDAO(tableMap);
		
		Mockito.when(rcaller.getMeanOfVector(Mockito.any(Double[].class))).thenReturn(2.0).thenReturn(1.0);
		StatisticGenerator stat = new StatisticGeneratorImpl(rcaller);
		
		HashMap<String, Double> expectedAverageViolationsMap = new HashMap<String, Double>();
		expectedAverageViolationsMap.put("r1", 2.0);
		expectedAverageViolationsMap.put("r2", 1.0);
		expectedAverageViolationsMap.put("r3", null);

		stat.setTableDAO(table);
		Assert.assertEquals(expectedAverageViolationsMap, stat.getAverageViolationsForAllRulesInTable());
	}
	
	@Test
	public void test_getAverageViolationsForAllRulesInTable_everyRuleHasZeroViolations() throws LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException, PropertyNotFoundException, NoTableSetForCalculatingStatsException, ConfigurationException, AverageCouldNotBeCalculatedException
	{
		ResourceInfoRow class1v1_row = TestUtils.getResourceInfoRow("class1", 1, 1, 1, ImmutableMap.of("r1",0, "r2", 0, "r3", 0));
		ResourceInfoRow class1v2_row = TestUtils.getResourceInfoRow("class1", 1, 1, 1, ImmutableMap.of("r1",0, "r2", 0, "r3", 0));
		ResourceInfoRow class2v1_row = TestUtils.getResourceInfoRow("class2", 1, 1, 1, ImmutableMap.of("r1",0, "r2", 0, "r3", 0));
		
		List<ResourceInfoRow> v1rows = new ArrayList<ResourceInfoRow>();
		v1rows.add(class1v1_row);
		v1rows.add(class2v1_row);
		List<ResourceInfoRow> v2rows = new ArrayList<ResourceInfoRow>();
		v2rows.add(class1v2_row);
		
		LinkedHashMap<String, List<ResourceInfoRow>> tableMap = new LinkedHashMap<String, List<ResourceInfoRow>>();
		tableMap.put("v1", v1rows);
		tableMap.put("v2", v2rows);
		
		TableDAO table = new TableDAO(tableMap);
		
		StatisticGenerator stat = new StatisticGeneratorImpl(rcaller);
		
		HashMap<String, Double> expectedAverageViolationsMap = new HashMap<String, Double>();
		expectedAverageViolationsMap.put("r1", null);
		expectedAverageViolationsMap.put("r2", null);
		expectedAverageViolationsMap.put("r3", null);

		stat.setTableDAO(table);
		Assert.assertEquals(expectedAverageViolationsMap, stat.getAverageViolationsForAllRulesInTable());
	}
	
	@Test
	public void test_getViolationDensityDencityColumnForRule_success() throws PropertyNotFoundException, NoTableSetForCalculatingStatsException
	{
		ResourceInfoRow class1v1row = TestUtils.getResourceInfoRow("class1", 1, null, 5, ImmutableMap.of("r1",5, "r2", 2));
		ResourceInfoRow class2v1row = TestUtils.getResourceInfoRow("class2", 3, null, 2, ImmutableMap.of("r1",4, "r2", 8));
		
		List<ResourceInfoRow> v1rows = new ArrayList<ResourceInfoRow>();
		v1rows.add(class1v1row);
		v1rows.add(class2v1row);
		
		LinkedHashMap<String, List<ResourceInfoRow>> toBeFilteredTable = new LinkedHashMap<String, List<ResourceInfoRow>>();
		toBeFilteredTable.put("v1", v1rows);
		
		TableDAO table = new TableDAO(toBeFilteredTable);
		
		
		Double[] expectedViolationsDensityColumn = new Double[]{1.0, 2.0};
		StatisticGenerator stat = new StatisticGeneratorImpl(rcaller);
		stat.setTableDAO(table);
		Assert.assertArrayEquals(expectedViolationsDensityColumn, stat.getViolationDensityColumnForRule("r1"));
	}
	
	@Test
	public void test_getViolationDensityDencityColumnForRule_emptyTable() throws PropertyNotFoundException, NoTableSetForCalculatingStatsException
	{
		LinkedHashMap<String, List<ResourceInfoRow>> toBeFilteredTable = new LinkedHashMap<String, List<ResourceInfoRow>>();
		
		TableDAO table = new TableDAO(toBeFilteredTable);
		
		Double[] expectedViolationsDensityColumn = new Double[]{};
		
		StatisticGenerator stat = new StatisticGeneratorImpl(rcaller);
		stat.setTableDAO(table);
		Assert.assertArrayEquals(expectedViolationsDensityColumn, stat.getViolationDensityColumnForRule("r1"));
	}
	
	@Test(expected=PropertyNotFoundException.class)
	public void test_getViolationDensityDencityColumnForRule_doesNotContainRule() throws PropertyNotFoundException, NoTableSetForCalculatingStatsException
	{
		ResourceInfoRow class1v1row = TestUtils.getResourceInfoRow("class1", 1, null, 5, ImmutableMap.of("r1",5, "r2", 2));
		ResourceInfoRow class2v1row = TestUtils.getResourceInfoRow("class2", 3, null, 2, ImmutableMap.of("r1",4, "r2", 8));	
		
		List<ResourceInfoRow> v1rows = new ArrayList<ResourceInfoRow>();
		v1rows.add(class1v1row);
		v1rows.add(class2v1row);
		
		LinkedHashMap<String, List<ResourceInfoRow>> toBeFilteredTable = new LinkedHashMap<String, List<ResourceInfoRow>>();
		toBeFilteredTable.put("v1", v1rows);
		
		TableDAO table = new TableDAO(toBeFilteredTable);
		StatisticGenerator stat = new StatisticGeneratorImpl(rcaller);
		stat.setTableDAO(table);
		stat.getViolationDensityColumnForRule("r4");
	}
	
	@Test
	public void test_getDefectInjectionFrequencyColumnForRule_success() throws PropertyNotFoundException, NoTableSetForCalculatingStatsException
	{
		ResourceInfoRow class1v1row = TestUtils.getResourceInfoRow("class1", 1, 13, 5, ImmutableMap.of("r1",5, "r2", 2));
		ResourceInfoRow class2v1row = TestUtils.getResourceInfoRow("class2", 3, 8, 2, ImmutableMap.of("r1",4, "r2", 8));
		
		List<ResourceInfoRow> v1Rows = new ArrayList<ResourceInfoRow>();
		v1Rows.add(class1v1row);
		v1Rows.add(class2v1row);
		
		LinkedHashMap<String, List<ResourceInfoRow>> toBeFilteredTable = new LinkedHashMap<String, List<ResourceInfoRow>>();
		toBeFilteredTable.put("v1", v1Rows);
		
		TableDAO table = new TableDAO(toBeFilteredTable);
		StatisticGenerator stat = new StatisticGeneratorImpl(rcaller);
		stat.setTableDAO(table);
		Double[] expectedDefectInjectionFrequencyColumn = new Double[]{1.0/13.0, 3.0/8.0};
		
		Assert.assertArrayEquals(expectedDefectInjectionFrequencyColumn, stat.getDefectInjectionFrequencyColumn());
	}

	@Test
	public void test_getPValue() throws AverageCouldNotBeCalculatedException, PropertyNotFoundException, LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException, PValueCouldNotBeCalculatedException, ConfigurationException
	{
		ResourceInfoRow class1v1_row = TestUtils.getResourceInfoRow("class1", 1, 1, 1, ImmutableMap.of("r1",2, "r2", 3, "r3", 4));
		ResourceInfoRow class2v1_row = TestUtils.getResourceInfoRow("class2", 1, 1, 1, ImmutableMap.of("r1",6, "r2", 5, "r3", 5));
		
		List<ResourceInfoRow> v1rows = new ArrayList<ResourceInfoRow>();
		v1rows.add(class1v1_row);
		v1rows.add(class2v1_row);
		
		LinkedHashMap<String, List<ResourceInfoRow>> tableMap = new LinkedHashMap<String, List<ResourceInfoRow>>();
		tableMap.put("v1", v1rows);
		
		TableDAO table = new TableDAO(tableMap);
		
		RCallerApi rcaller = new RCallerApiImpl(new MindConfigurationImpl());
		StatisticGenerator stat = new StatisticGeneratorImpl(rcaller);
		stat.setTableDAO(table);
		Double expected = 0.9639;
		Assert.assertEquals(expected, stat.getPvalue(), 0.0001);
	}
	
	@Test
	public void test_getPValue_lengthOfColumnsDiffer() throws AverageCouldNotBeCalculatedException, PropertyNotFoundException, LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException, PValueCouldNotBeCalculatedException, ConfigurationException
	{
		ResourceInfoRow class1v1_row = TestUtils.getResourceInfoRow("class1", 1, 1, 1, ImmutableMap.of("r1",2, "r2", 0, "r3", 0));
		ResourceInfoRow class2v1_row = TestUtils.getResourceInfoRow("class2", 1, 1, 1, ImmutableMap.of("r1",6, "r2", 0, "r3", 5));
		
		List<ResourceInfoRow> v1rows = new ArrayList<ResourceInfoRow>();
		v1rows.add(class1v1_row);
		v1rows.add(class2v1_row);
		
		LinkedHashMap<String, List<ResourceInfoRow>> tableMap = new LinkedHashMap<String, List<ResourceInfoRow>>();
		tableMap.put("v1", v1rows);
		
		TableDAO table = new TableDAO(tableMap);
		
		RCallerApi rcaller = new RCallerApiImpl(new MindConfigurationImpl());
		StatisticGenerator stat = new StatisticGeneratorImpl(rcaller);
		stat.setTableDAO(table);
		Double expected = 1.0;
		Assert.assertEquals(expected, stat.getPvalue(), 0.00001);
	}
}
