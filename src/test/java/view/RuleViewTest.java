package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Test;

import testutils.TestUtils;

import com.google.common.collect.ImmutableMap;

import dao.ResourceInfoRowDAO;
import dao.StatisticsDAO;
import dao.TableDAO;

public class RuleViewTest {
	@Test
	public void test_getRuleView()
	{
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
		HashMap<String, Integer> rankOfRules = new HashMap<String, Integer>();
		rankOfRules.put("r1", 1);
		rankOfRules.put("r3", 2);
		rankOfRules.put("r2", 3);
		stats.setRankOfRules(rankOfRules);
		HashMap<String, Integer> numberOfViolationsThroughoutAllVersions = new HashMap<String, Integer>();
		numberOfViolationsThroughoutAllVersions.put("r1", 4);
		numberOfViolationsThroughoutAllVersions.put("r2", 7);
		numberOfViolationsThroughoutAllVersions.put("r3", 7);
		stats.setNumberOfViolationsThroughoutAllVersions(numberOfViolationsThroughoutAllVersions);
		System.out.println(RuleView.getRuleViewTable(stats).write());
	}
}
