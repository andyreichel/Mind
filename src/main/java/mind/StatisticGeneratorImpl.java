package mind;

import interfaces.RCallerApi;
import interfaces.StatisticGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dao.TableDAO;
import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.PropertyNotFoundException;
import exceptions.RankCouldNotBeCalculatedException;

public class StatisticGeneratorImpl implements StatisticGenerator {
	RCallerApi rcaller;
	
	StatisticGeneratorImpl(RCallerApi rcaller)
	{
		this.rcaller = rcaller;
	}
	
	public List<Double> getSpearmanCoefficientForAllRulesInTable(TableDAO table) throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException
	{
		Set<String> allRules = table.getAllRulesInTable();
		Double[] defectInjectionFrequencyColumn = table.getDefectInjectionFrequencyColumnForRule();
		List<Double> ranks= new ArrayList<Double>();
		 for(String rule : allRules)
		 {
			 Double coeff;
			 try
			 {
				 coeff = rcaller.getSpearmanCoefficient(defectInjectionFrequencyColumn, table.getViolationDensityDencityColumnForRule(rule));	 
			 }catch(RankCouldNotBeCalculatedException re)
			 {
				 coeff = null;
			 }
			 ranks.add(coeff);
		 }
		 return ranks;
	}
	
	

	 

}
