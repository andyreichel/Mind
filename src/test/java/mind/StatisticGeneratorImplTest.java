package mind;

import interfaces.RCallerApi;
import interfaces.StatisticGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import dao.TableDAO;
import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.PropertyNotFoundException;
import exceptions.RankCouldNotBeCalculatedException;

@RunWith(MockitoJUnitRunner.class)
public class StatisticGeneratorImplTest {
	@Mock
	RCallerApi rcaller;
	
	@Test
	public void getSpearmanCoefficientForAllRulesInTable_success() throws LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException, PropertyNotFoundException
	{
		HashMap<String, Integer> class1v1_row = new HashMap<String, Integer>();
		class1v1_row.put("numberDefects", 1);
		class1v1_row.put("locTouched", 13);
		class1v1_row.put("size", 5);
		class1v1_row.put("r1", 5);
		class1v1_row.put("r2", 2);
		class1v1_row.put("r3", 0);
		
		HashMap<String, Integer> class2v1_row = new HashMap<String, Integer>();
		class2v1_row.put("numberDefects", 3);
		class2v1_row.put("locTouched", 8);
		class2v1_row.put("size", 2);
		class2v1_row.put("r1", 4);
		class2v1_row.put("r2", 8);
		class2v1_row.put("r3", 0);
		
		HashMap<String, Integer> class1v2_row = new HashMap<String, Integer>();
		class1v2_row.put("numberDefects", 1);
		class1v2_row.put("locTouched", 1);
		class1v2_row.put("size", 3);
		class1v2_row.put("r1", 0);
		class1v2_row.put("r2", 2);
		class1v2_row.put("r3", 0);
		
		
		HashMap<String, HashMap<String, Integer>> v1rows = new HashMap<String, HashMap<String,Integer>>();
		v1rows.put("class1", class1v1_row);
		v1rows.put("class2", class2v1_row);
		HashMap<String, HashMap<String, Integer>> v2rows = new HashMap<String, HashMap<String,Integer>>();
		v2rows.put("class1", class1v2_row);
		
		LinkedHashMap<String, HashMap<String, HashMap<String, Integer>>> tableMap = new LinkedHashMap<String, HashMap<String, HashMap<String, Integer>>>();
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
		Assert.assertEquals(expectedRankList, stat.getSpearmanCoefficientForAllRulesInTable(table));
	}
}
