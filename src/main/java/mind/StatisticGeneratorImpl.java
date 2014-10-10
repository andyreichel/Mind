package mind;

import interfaces.RCallerApi;
import interfaces.StatisticGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.google.inject.Inject;

import dao.ResourceInfoRow;
import dao.TableDAO;
import exceptions.AverageCouldNotBeCalculatedException;
import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.NoTableSetForCalculatingStatsException;
import exceptions.PValueCouldNotBeCalculatedException;
import exceptions.PropertyNotFoundException;
import exceptions.RankCouldNotBeCalculatedException;

public class StatisticGeneratorImpl implements StatisticGenerator {
	RCallerApi rcaller;
	TableDAO table;
	
	
	@Inject
	StatisticGeneratorImpl(RCallerApi rcaller)
	{
		this.rcaller = rcaller;
	}
	
	public void setTableDAO(TableDAO table)
	{
		this.table = table;
	}
	
	public HashMap<String, Double> getSpearmanCoefficientForAllRulesInTable() throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException
	{
		if(table == null)
		{
			throw new NoTableSetForCalculatingStatsException("Please set a table");
		}
		Set<String> allRules = table.getAllRulesInTable();
		Double[] defectInjectionFrequencyColumn = getDefectInjectionFrequencyColumn();
		HashMap<String, Double> ranks= new HashMap<String,Double>();
		 for(String rule : allRules)
		 {
			 try
			 {
				 ranks.put(rule, rcaller.getSpearmanCoefficient(defectInjectionFrequencyColumn, getViolationDensityColumnForRule(rule)));	 
			 }catch(RankCouldNotBeCalculatedException re)
			 {
				 ranks.put(rule, null);
			 }
		 }
		 return ranks;
	}

	public HashMap<String, Double> getAverageViolationsForAllRulesInTable() throws PropertyNotFoundException,LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException {
		if(table == null)
			throw new NoTableSetForCalculatingStatsException("Please set a table");
		
		HashMap<String, List<Double>> coefficientsBetweenDefInjAndViolationDensForRules = getCoefficientsBetweenDefInjAndViolationsDensForRules();
		HashMap<String, Double> averageViolationsForAllRules = new HashMap<String, Double>();
		for(String rule : coefficientsBetweenDefInjAndViolationDensForRules.keySet())
		{
			try
			{
				List<Double> coefficientsBetweenDefInjAndViolationDens = coefficientsBetweenDefInjAndViolationDensForRules.get(rule);
				Double[] array = coefficientsBetweenDefInjAndViolationDensForRules.get(rule).toArray(new Double[coefficientsBetweenDefInjAndViolationDens.size()]);
				if(array.length!=0)
				{
					averageViolationsForAllRules.put(rule, rcaller.getMeanOfVector(array));
				}
				else
				{
					averageViolationsForAllRules.put(rule, null);
				}
			}catch(AverageCouldNotBeCalculatedException ae)
			{
				averageViolationsForAllRules.put(rule, null);
			}
		}
		return averageViolationsForAllRules;
	}

	private HashMap<String, List<Double>>  getCoefficientsBetweenDefInjAndViolationsDensForRules() throws PropertyNotFoundException, NoTableSetForCalculatingStatsException
	{
		Double[] defInjFreq = getDefectInjectionFrequencyColumn();
		HashMap<String, List<Double>> coefficientsBetweenDefInjAndViolationDensForRules = new HashMap<String, List<Double>>();
		for(String rule : table.getAllRulesInTable())
		{
			Double[] violationDensity = getViolationDensityColumnForRule(rule);
			List<Double> coefficientsBetweenDefInjAndViolationDens = new ArrayList<Double>();
			
			for(int i = 0; i < violationDensity.length; i++)
			{
				if(!violationDensity[i].equals(0.0))
				{
					coefficientsBetweenDefInjAndViolationDens.add(defInjFreq[i]/violationDensity[i]);
				}
			}
			coefficientsBetweenDefInjAndViolationDensForRules.put(rule, coefficientsBetweenDefInjAndViolationDens);
		}
		return coefficientsBetweenDefInjAndViolationDensForRules;
	}
	
	public Double getPvalue() throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException, PValueCouldNotBeCalculatedException
	{
		HashMap<String, List<Double>> coefficientsBetweenDefInjAndViolationsDensForRules = getCoefficientsBetweenDefInjAndViolationsDensForRules();
		
		List<double[]> coefficientsForAllRules = new ArrayList<double[]>();
		for(String rule : coefficientsBetweenDefInjAndViolationsDensForRules.keySet())
		{
			Double[] doubleArray = coefficientsBetweenDefInjAndViolationsDensForRules.get(rule).toArray(new Double[coefficientsBetweenDefInjAndViolationsDensForRules.get(rule).size()]);
			coefficientsForAllRules.add(ArrayUtils.toPrimitive(doubleArray));
		}
		double[][] inputForR =  coefficientsForAllRules.toArray(new double[coefficientsForAllRules.size()][]);
		return rcaller.getPvalue(inputForR);
	}
	
	public Double[] getViolationDensityColumnForRule(String rule) throws PropertyNotFoundException, NoTableSetForCalculatingStatsException
	{
		if(table == null)
			throw new NoTableSetForCalculatingStatsException("Please set a table");
		List<Double> defectInjectionFrequencyColumn = new ArrayList<Double>();
		for(String version : table.getVersions())
		{
			for(ResourceInfoRow resourceRow : table.getResourceInfoRowsForVersion(version))
			{
				double numberOfViolationsOfRule = resourceRow.getNumberOfViolationsForRule(rule);
				double size = resourceRow.getSize();
				defectInjectionFrequencyColumn.add(numberOfViolationsOfRule/size);
			}
		}
		Double[] array = new Double[defectInjectionFrequencyColumn.size()];
		for(int i = 0; i < defectInjectionFrequencyColumn.size(); i++) array[i] = defectInjectionFrequencyColumn.get(i);
		return array;

	}
	
	public Double[] getDefectInjectionFrequencyColumn() throws PropertyNotFoundException, NoTableSetForCalculatingStatsException
	{
		if(table == null)
			throw new NoTableSetForCalculatingStatsException("Please set a table");
		List<Double> defectInjectionFrequencyColumn = new ArrayList<Double>();
		for(String version : table.getVersions())
		{
			for(ResourceInfoRow resourceRow : table.getResourceInfoRowsForVersion(version))
			{
				double numberOfDefects = resourceRow.getNumberDefects();
				double locTouched = resourceRow.getLocTouched();
				defectInjectionFrequencyColumn.add(numberOfDefects/locTouched);
			}
		}
		Double[] array = new Double[defectInjectionFrequencyColumn.size()];
		for(int i = 0; i < defectInjectionFrequencyColumn.size(); i++) array[i] = defectInjectionFrequencyColumn.get(i);
		return array;
	}
}
