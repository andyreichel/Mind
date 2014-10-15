package mind;

import interfaces.RCallerApi;
import interfaces.StatisticGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.collect.FluentIterable;
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
	private Double[] stat_defectInjectionFrequencyColumn;
	private Double stat_pValue;
	private HashMap<String, Integer> stat_numberOfViolationsThroughoutAllVersions;
	HashMap<String, Integer> stat_rankOfRules;
	
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
		stats.setDefectInjectionFrequencyColumn(getDefectInjectionFrequencyColumn());
		stats.setpValue(getPvalue());
		stats.setSpearmanCoefficientForAllRules(getSpearmanCoefficientForAllRulesInTable());
		stats.setViolationDensityForAllRules(getViolationDensityForRules());
		stats.setRankOfRules(getRankOfRules());
		stats.setNumberOfViolationsThroughoutAllVersions(getNumberOfViolationsThroughoutAllVersions());
		return stats;
	}
	
	public HashMap<String, Integer> getRankOfRules() throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException
	{
		if(stat_rankOfRules == null)
			generateRankOfRules();
		return stat_rankOfRules;
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
		if(stat_defectInjectionFrequencyColumn == null)
			generateDefectInjectionFrequencyColumn();
		return stat_defectInjectionFrequencyColumn;
		
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
		stat_defectInjectionFrequencyColumn = array;
	}
	
	public HashMap<String, Integer> getNumberOfViolationsThroughoutAllVersions() throws PropertyNotFoundException
	{
		if(stat_numberOfViolationsThroughoutAllVersions == null)
			generateNumberOfViolationsThroughoutAllVersions();
		return stat_numberOfViolationsThroughoutAllVersions;
	}
	
	public void generateNumberOfViolationsThroughoutAllVersions() throws PropertyNotFoundException
	{
		HashMap<String, Integer> numberOfViolationsMap = new HashMap<String, Integer>();
		for(String rule : table.getAllRulesInTable())
		{
			numberOfViolationsMap.put(rule, getNumberOfViolationsForRuleThroughoutAllVersions(rule));
		}
		stat_numberOfViolationsThroughoutAllVersions = numberOfViolationsMap;
	}
	
	private Integer getNumberOfViolationsForRuleThroughoutAllVersions(String rule) throws PropertyNotFoundException
	{
		Integer numberOfViolations = 0;
		for(String version : table.getVersions())
		{
			for(ResourceInfoRowDAO row : table.getResourceInfoRowsForVersion(version))
			{
				numberOfViolations += row.getNumberOfViolationsForRule(rule);
			}
		}
		return numberOfViolations;
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

	public void generateRankOfRules() throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException 
	{
		
		HashMap<String, Integer> rankMap = new HashMap<String, Integer>();
		HashMap<String, Double> spearmanForAllRules = new HashMap<String, Double>(getSpearmanCoefficientForAllRulesInTable());
		List<Map.Entry<String, Double>> nullValueEntries = getAllEntriesThatsValueIsNull(spearmanForAllRules);
		for(Map.Entry<String, Double> entry : nullValueEntries)
		{
			spearmanForAllRules.remove(entry.getKey());
		}
		SortedSet<Map.Entry<String, Double>> sortedMap = entriesSortedByValues(spearmanForAllRules);
		int rank = 1;
		for(Map.Entry<String, Double> rule : sortedMap)
		{
			rankMap.put(rule.getKey(), rank);
			rank++;
		}
		
		for(Map.Entry<String, Double> entry : nullValueEntries)
		{
			rankMap.put(entry.getKey(), rank);
		}
		
		stat_rankOfRules = rankMap;
	}
	
	private List<Map.Entry<String, Double>> getAllEntriesThatsValueIsNull(HashMap<String, Double> map)
	{
		List<Map.Entry<String, Double>> nullValueEntries = new ArrayList<Map.Entry<String, Double>>();
		for(Map.Entry<String, Double> entry : map.entrySet())
		{
			if(entry.getValue() == null)
				nullValueEntries.add(entry);
		}
		return nullValueEntries;
	}
	
	static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
            new Comparator<Map.Entry<K,V>>() {
                public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                	
                    int res = e1.getValue().compareTo(e2.getValue());
                    return res != 0 ? res : 1; // Special fix to preserve items with equal values
                }
            }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
