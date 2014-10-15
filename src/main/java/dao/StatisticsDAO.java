package dao;

import interfaces.RCallerApi;

import java.io.ObjectInputStream.GetField;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 
 * Data access object that stores the statistics of one analyze.
 * Statistics are:
 * Spearman coefficient describes how many defects will be produced when a rule is violated. The higher the worse
 * Average for rules
 * PValue that describes how good the distribution of violations is
 *
 */
public class StatisticsDAO {
	private HashMap<String, Double> spearmanCoefficientForAllRules;
	private HashMap<String, Double> averageForAllRules;
	private HashMap<String, List<Double> > coefficientsBetweenDefInjAndViolationsDensForRules;
	private HashMap<String, Double[]> violationDensityForAllRules;
	private Double[] defectInjectionFrequencyForAllRules;
	private Double pValue;
	private HashMap<String, Integer> rankOfRules;
	private HashMap<String, Integer> numberOfViolationsThroughoutAllVersions;
	
	public HashMap<String, Double> getSpearmanCoefficientForAllRules() {
		return spearmanCoefficientForAllRules;
	}
	public void setSpearmanCoefficientForAllRules(
			HashMap<String, Double> spearmanCoefficientForAllRules) {
		this.spearmanCoefficientForAllRules = spearmanCoefficientForAllRules;
	}
	public HashMap<String, Double> getAverageForAllRules() {
		return averageForAllRules;
	}
	public void setAverageForAllRules(HashMap<String, Double> averageForAllRules) {
		this.averageForAllRules = averageForAllRules;
	}
	public HashMap<String, List<Double>> getCoefficientsBetweenDefInjAndViolationsDensForRules() {
		return coefficientsBetweenDefInjAndViolationsDensForRules;
	}
	public void setCoefficientsBetweenDefInjAndViolationsDensForRules(
			HashMap<String, List<Double>> coefficientsBetweenDefInjAndViolationsDensForRules) {
		this.coefficientsBetweenDefInjAndViolationsDensForRules = coefficientsBetweenDefInjAndViolationsDensForRules;
	}
	public HashMap<String, Double[]> getViolationDensityForAllRules() {
		return violationDensityForAllRules;
	}
	public void setViolationDensityForAllRules(
			HashMap<String, Double[]> violationDensityForAllRules) {
		this.violationDensityForAllRules = violationDensityForAllRules;
	}
	public Double[] getDefectInjectionFrequencyForAllRules() {
		return defectInjectionFrequencyForAllRules;
	}
	public void setDefectInjectionFrequencyColumn(
			Double[] defectInjectionFrequencyForAllRules) {
		this.defectInjectionFrequencyForAllRules = defectInjectionFrequencyForAllRules;
	}
	
	
	public Integer getNumberOfViolationsThroughoutAllVersions(String rule)
	{
		return numberOfViolationsThroughoutAllVersions.get(rule);
	}
	
	public void setNumberOfViolationsThroughoutAllVersions(HashMap<String, Integer> numberOfViolationsThroughoutAllVersions)
	{
		this.numberOfViolationsThroughoutAllVersions = numberOfViolationsThroughoutAllVersions;
	}
	
	public Double getpValue() {
		return pValue;
	}
	public void setpValue(Double pValue) {
		this.pValue = pValue;
	}
	
	public void setRankOfRules(HashMap<String, Integer> rankOfRules)
	{
		this.rankOfRules = rankOfRules;
	}
	
	public HashMap<String, Integer> getRankOfRules()
	{
		return rankOfRules;
	}
	
	public String toString()
	{
		StringBuilder statisticOverview = new StringBuilder();
		statisticOverview.append("########### STATS #########\n");
		
		for(String rule : averageForAllRules.keySet())
		{
			statisticOverview.append(rule + ":\n");
			statisticOverview.append("avg: " + averageForAllRules.get(rule));
			statisticOverview.append("\t");
			statisticOverview.append("roh: " + spearmanCoefficientForAllRules.get(rule));
			statisticOverview.append("\n");
		}
		statisticOverview.append("P-value: " + pValue);
		return statisticOverview.toString();
	}
}
