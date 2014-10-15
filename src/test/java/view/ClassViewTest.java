package view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Test;

import testutils.TestUtils;

import com.google.common.collect.ImmutableMap;

import dao.ResourceInfoRowDAO;
import dao.TableDAO;

public class ClassViewTest {
	@Test
	public void test_getRuleView()
	{
		List<ResourceInfoRowDAO> rowsv1 = new ArrayList<ResourceInfoRowDAO>();
		rowsv1.add(TestUtils.getResourceInfoRow("class1", 5, null, 500, ImmutableMap.of("r1", 0, "r2", 5)));
		List<ResourceInfoRowDAO> rowsv2 = new ArrayList<ResourceInfoRowDAO>();
		rowsv2.add(TestUtils.getResourceInfoRow("class1", 5, null, 500, ImmutableMap.of("r1", 0, "r2", 5)));
		LinkedHashMap<String, List<ResourceInfoRowDAO>> table = new LinkedHashMap<String, List<ResourceInfoRowDAO>>();
		table.put("v1", rowsv1);
		table.put("v2", rowsv2);
		TableDAO tableDao = new TableDAO(table);
		
//		StatisticsDAO stats = new StatisticsDAO();
//		HashMap<String, Double> averageForAllRules = new HashMap<String, Double>();
//		averageForAllRules.put("r1", 5.0);
//		averageForAllRules.put("r2", null);
//		averageForAllRules.put("r3", 3.0);
//		stats.setAverageForAllRules(averageForAllRules);
//		HashMap<String, Double> spearmanRank = new HashMap<String, Double>();
//		spearmanRank.put("r1", 1.0);
//		spearmanRank.put("r2", null);
//		spearmanRank.put("r3", 2.0);
//		stats.setSpearmanCoefficientForAllRules(spearmanRank);
		
		System.out.println(ClassView.getClassViewTable(tableDao).write());
	}
}
