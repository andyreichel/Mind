package dao;

import interfaces.RCallerApi;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class StatisticsDAO {
	private HashMap<String, Double> spearmanCoefficientForAllRules;
	private HashMap<String, Double> averageForAllRules;
	private HashMap<String, List<Double> > coefficientsBetweenDefInjAndViolationsDensForRules;
	private HashMap<String, Double[]> violationDensityForAllRules;
	private Double[] defectInjectionFrequencyForAllRules;
	private Double pValue;
	
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
	public void setDefectInjectionFrequencyForAllRules(
			Double[] defectInjectionFrequencyForAllRules) {
		this.defectInjectionFrequencyForAllRules = defectInjectionFrequencyForAllRules;
	}
	public Double getpValue() {
		return pValue;
	}
	public void setpValue(Double pValue) {
		this.pValue = pValue;
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
