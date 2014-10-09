package mind;

import interfaces.RCallerApi;
import interfaces.StatisticGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;

import dao.ResourceInfoRow;
import dao.TableDAO;
import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.NoTableSetForCalculatingStatsException;
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
			 Double coeff;
			 try
			 {
				 coeff = rcaller.getSpearmanCoefficient(defectInjectionFrequencyColumn, getViolationDensityDencityColumnForRule(rule));	 
			 }catch(RankCouldNotBeCalculatedException re)
			 {
				 coeff = null;
			 }
			 ranks.put(rule, coeff);
		 }
		 return ranks;
	}

	public HashMap<String, Double> getAverageViolationsForAllRulesInTable(
			TableDAO table) throws PropertyNotFoundException,
			LenghtOfDoubleArraysDifferException {
		
		return null;
	}


	public Double[] getViolationDensityDencityColumnForRule(String rule) throws PropertyNotFoundException
	{
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
	
	public Double[] getDefectInjectionFrequencyColumn() throws PropertyNotFoundException
	{
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
