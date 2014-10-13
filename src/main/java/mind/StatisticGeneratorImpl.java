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

import dao.ResourceInfoRowDAO;
import dao.StatisticsDAO;
import dao.TableDAO;
import exceptions.AverageCouldNotBeCalculatedException;
import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.NoTableSetForCalculatingStatsException;
import exceptions.PValueCouldNotBeCalculatedException;
import exceptions.PropertyNotFoundException;
import exceptions.RankCouldNotBeCalculatedException;

public class StatisticGeneratorImpl implements StatisticGenerator {
	private RCallerApi rcaller;
	private TableDAO table;
	private HashMap<String, Double> stat_spearmanCoefficientForAllRules;
	private HashMap<String, Double> stat_averageForAllRules;
	private HashMap<String, List<Double> > stat_coefficientsBetweenDefInjAndViolationsDensForRules;
	private Set<String> stat_allRulesOfTable;
	private HashMap<String, Double[]> stat_violationDensityForAllRules;
	private Double[] stat_defectInjectionFrequencyForAllRules;
	private Double stat_pValue;
	
	@Inject
	StatisticGeneratorImpl(RCallerApi rcaller)
	{
		this.rcaller = rcaller;
	}
	
	public void setTableDAO(TableDAO table) throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException
	{
		this.table = table;
		stat_allRulesOfTable = table.getAllRulesInTable();
	}
	
	public StatisticsDAO generateStatistcs(TableDAO table) throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException, PValueCouldNotBeCalculatedException
	{
		StatisticsDAO stats = new StatisticsDAO();
		setTableDAO(table);
		stats.setAverageForAllRules(getAverageViolationsForAllRulesInTable());
		stats.setCoefficientsBetweenDefInjAndViolationsDensForRules(getCoefficientsBetweenDefInjAndViolationsDensForRules());
		stats.setDefectInjectionFrequencyForAllRules(getDefectInjectionFrequencyColumn());
		stats.setpValue(getPvalue());
		stats.setSpearmanCoefficientForAllRules(getSpearmanCoefficientForAllRulesInTable());
		stats.setViolationDensityForAllRules(getViolationDensityForRules());
		return stats;
	}
	
	public HashMap<String, Double> getSpearmanCoefficientForAllRulesInTable() throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException
	{
		if(stat_spearmanCoefficientForAllRules==null)
			generateSpearmanCoefficientForAllRulesInTable();
		return stat_spearmanCoefficientForAllRules;
	}
	
	public HashMap<String, Double> getAverageViolationsForAllRulesInTable() throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException
	{
		if(stat_averageForAllRules==null)
			generateAverageViolationsForAllRulesInTable();
		return stat_averageForAllRules;
	}
	

	public HashMap<String, List<Double>> getCoefficientsBetweenDefInjAndViolationsDensForRules() throws PropertyNotFoundException, NoTableSetForCalculatingStatsException
	{
		if(stat_coefficientsBetweenDefInjAndViolationsDensForRules == null)
			generateCoefficientsBetweenDefInjAndViolationsDensForRules();
		
		return stat_coefficientsBetweenDefInjAndViolationsDensForRules;
	}
	
	public Double getPvalue() throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException, PValueCouldNotBeCalculatedException
	{
		if(stat_pValue == null)
			generatePvalue();
		return stat_pValue;
	}

	
	public HashMap<String, Double[]> getViolationDensityForRules() throws NoTableSetForCalculatingStatsException, PropertyNotFoundException
	{
		if(stat_violationDensityForAllRules == null)
			generateViolationDensityForRules();
		return stat_violationDensityForAllRules;
	}
	
	public Double[] getDefectInjectionFrequencyColumn() throws PropertyNotFoundException, NoTableSetForCalculatingStatsException
	{
		if(stat_defectInjectionFrequencyForAllRules == null)
			generateDefectInjectionFrequencyColumn();
		return stat_defectInjectionFrequencyForAllRules;
		
	}
	
	private void generateDefectInjectionFrequencyColumn() throws PropertyNotFoundException, NoTableSetForCalculatingStatsException
	{
		if(table == null)
			throw new NoTableSetForCalculatingStatsException("Please set a table");
		List<Double> defectInjectionFrequencyColumn = new ArrayList<Double>();
		for(String version : table.getVersions())
		{
			for(ResourceInfoRowDAO resourceRow : table.getResourceInfoRowsForVersion(version))
			{
				Integer numberOfDefects = resourceRow.getNumberDefects();
				Integer locTouched = resourceRow.getLocTouched();
				
				if(locTouched == null || locTouched == 0)
				{
					defectInjectionFrequencyColumn.add(null);
				}else
				{
					defectInjectionFrequencyColumn.add(numberOfDefects.doubleValue()/locTouched.doubleValue());	
				}
			}
		}
		Double[] array = new Double[defectInjectionFrequencyColumn.size()];
		for(int i = 0; i < defectInjectionFrequencyColumn.size(); i++) array[i] = defectInjectionFrequencyColumn.get(i);
		stat_defectInjectionFrequencyForAllRules = array;
	}
	
	public Double[] getViolationDensityForRule(String rule) throws PropertyNotFoundException, NoTableSetForCalculatingStatsException
	{
		if(stat_violationDensityForAllRules == null)
			generateViolationDensityForRules();
		if(!stat_violationDensityForAllRules.containsKey(rule))
			throw new PropertyNotFoundException(rule + " was not found in table");
		return stat_violationDensityForAllRules.get(rule);
	}
	
	private void generateViolationDensityForRules() throws NoTableSetForCalculatingStatsException, PropertyNotFoundException
	{
		if(table == null)
			throw new NoTableSetForCalculatingStatsException("Please set a table");
		HashMap<String, Double[]> violationDensityColumnForRules = new HashMap<String, Double[]>();
		for(String rule : stat_allRulesOfTable)
		{
			violationDensityColumnForRules.put(rule, getViolationDensityColumnForRule(rule));
		}
		
		stat_violationDensityForAllRules =  violationDensityColumnForRules;
	}
	
	private Double[] getViolationDensityColumnForRule(String rule) throws NoTableSetForCalculatingStatsException, PropertyNotFoundException
	{
		if(table == null)
			throw new NoTableSetForCalculatingStatsException("Please set a table");
		List<Double> defectInjectionFrequencyColumn = new ArrayList<Double>();
		for(String version : table.getVersions())
		{
			for(ResourceInfoRowDAO resourceRow : table.getResourceInfoRowsForVersion(version))
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
	
	
	private void generatePvalue() throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException, PValueCouldNotBeCalculatedException
	{
		if(table == null)
		{
			throw new NoTableSetForCalculatingStatsException("Please set a table");
		}
		HashMap<String, List<Double>> coefficientsBetweenDefInjAndViolationsDensForRules = getCoefficientsBetweenDefInjAndViolationsDensForRules();
		
		List<double[]> coefficientsForAllRules = new ArrayList<double[]>();
		for(String rule : coefficientsBetweenDefInjAndViolationsDensForRules.keySet())
		{
			Double[] doubleArray = coefficientsBetweenDefInjAndViolationsDensForRules.get(rule).toArray(new Double[coefficientsBetweenDefInjAndViolationsDensForRules.get(rule).size()]);
			coefficientsForAllRules.add(ArrayUtils.toPrimitive(doubleArray));
		}
		double[][] inputForR =  coefficientsForAllRules.toArray(new double[coefficientsForAllRules.size()][]);
		stat_pValue = rcaller.getPvalue(inputForR);
	}
	
	private void generateCoefficientsBetweenDefInjAndViolationsDensForRules() throws PropertyNotFoundException, NoTableSetForCalculatingStatsException
	{
		if(table == null)
		{
			throw new NoTableSetForCalculatingStatsException("Please set a table");
		}
		HashMap<String, List<Double>> coefficientsBetweenDefInjAndViolationDensForRules = new HashMap<String, List<Double>>();
		for(String rule : stat_allRulesOfTable)
		{
			Double[] violationDensity = getViolationDensityForRules().get(rule);
			List<Double> coefficientsBetweenDefInjAndViolationDens = new ArrayList<Double>();
			
			for(int i = 0; i < violationDensity.length; i++)
			{
				if(!violationDensity[i].equals(0.0) && getDefectInjectionFrequencyColumn()[i] != null)
				{
					coefficientsBetweenDefInjAndViolationDens.add(getDefectInjectionFrequencyColumn()[i]/violationDensity[i]);
				}
			}
			coefficientsBetweenDefInjAndViolationDensForRules.put(rule, coefficientsBetweenDefInjAndViolationDens);
		}
		stat_coefficientsBetweenDefInjAndViolationsDensForRules = coefficientsBetweenDefInjAndViolationDensForRules;
	}
	
	private void generateAverageViolationsForAllRulesInTable() throws PropertyNotFoundException,LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException {
		if(table == null)
			throw new NoTableSetForCalculatingStatsException("Please set a table");
		
		HashMap<String, Double> averageViolationsForAllRules = new HashMap<String, Double>();
		for(String rule : getCoefficientsBetweenDefInjAndViolationsDensForRules().keySet())
		{
			try
			{
				List<Double> coefficientsBetweenDefInjAndViolationDens = getCoefficientsBetweenDefInjAndViolationsDensForRules().get(rule);
				Double[] array = getCoefficientsBetweenDefInjAndViolationsDensForRules().get(rule).toArray(new Double[coefficientsBetweenDefInjAndViolationDens.size()]);
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
		stat_averageForAllRules = averageViolationsForAllRules;
	}
	
	private void generateSpearmanCoefficientForAllRulesInTable() throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException
	{
		if(table == null)
		{
			throw new NoTableSetForCalculatingStatsException("Please set a table");
		}
		HashMap<String, Double> mapOfRoh= new HashMap<String,Double>();
		 for(String rule : stat_allRulesOfTable)
		 {
			 try
			 {
				 mapOfRoh.put(rule, rcaller.getSpearmanCoefficient(getDefectInjectionFrequencyColumn(), getViolationDensityForRules().get(rule)));	 
			 }catch(RankCouldNotBeCalculatedException re)
			 {
				 mapOfRoh.put(rule, null);
			 }
		 }
		 stat_spearmanCoefficientForAllRules = mapOfRoh;
	}
}
