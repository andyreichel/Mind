package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import testutils.TestUtils;

import com.google.common.collect.ImmutableMap;

import dao.ResourceInfoRowDAO;
import dao.StatisticsDAO;
import dao.TableDAO;

public class HTMLBuilderTest {
	@Test
	public void test_getHtmlForTableWithCodeInfo()
	{
		List<ResourceInfoRowDAO> rowsv1 = new ArrayList<ResourceInfoRowDAO>();
		rowsv1.add(TestUtils.getResourceInfoRow("class1", 5, null, 500, ImmutableMap.of("r1", 0, "r2", 5)));
		List<ResourceInfoRowDAO> rowsv2 = new ArrayList<ResourceInfoRowDAO>();
		rowsv2.add(TestUtils.getResourceInfoRow("class1", 5, null, 500, ImmutableMap.of("r1", 0, "r2", 5)));
		LinkedHashMap<String, List<ResourceInfoRowDAO>> table = new LinkedHashMap<String, List<ResourceInfoRowDAO>>();
		table.put("v1", rowsv1);
		table.put("v2", rowsv2);
		TableDAO tableDao = new TableDAO(table);
		
		StatisticsDAO stats = new StatisticsDAO();
		HashMap<String, Double> averageForAllRules = new HashMap<String, Double>();
		averageForAllRules.put("r1", 5.0);
		averageForAllRules.put("r2", null);
		averageForAllRules.put("r3", 3.0);
		stats.setAverageForAllRules(averageForAllRules);
		HashMap<String, Double> spearmanRank = new HashMap<String, Double>();
		spearmanRank.put("r1", 1.0);
		spearmanRank.put("r2", null);
		spearmanRank.put("r3", 2.0);
		stats.setSpearmanCoefficientForAllRules(spearmanRank);
		
		String expectedHTML = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>MIND</title><link href=\"./CSS/style.css\" rel=\"stylesheet\" type=\"text/css\"></link></head><body><h1>Table with information.</h1><table cellspacing=\"\"0\"\"><thead><tr><th>Resource</th><th>Version</th><th>Number of Defects</th><th>Size</th><th>lines of code touched</th><th>r1</th><th>r2</th></tr></thead><tbody><tr><td>class1</td><td>v1</td><td>5</td><td>500</td><td>null</td><td>0</td><td>5</td></tr><tr><td>class1</td><td>v2</td><td>5</td><td>500</td><td>null</td><td>0</td><td>5</td></tr></tbody></table></body></html>";
		Assert.assertEquals(expectedHTML, HTMLBuilder.getHtmlPage(tableDao, stats));
	}
}
