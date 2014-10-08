package mind;

import interfaces.RCallerApi;
import interfaces.StatisticGenerator;

import java.util.HashMap;
import java.util.Set;

import com.google.inject.Inject;

import dao.TableDAO;
import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.PropertyNotFoundException;
import exceptions.RankCouldNotBeCalculatedException;

public class StatisticGeneratorImpl implements StatisticGenerator {
	RCallerApi rcaller;
	
	@Inject
	StatisticGeneratorImpl(RCallerApi rcaller)
	{
		this.rcaller = rcaller;
	}
	
	public HashMap<String, Double> getSpearmanCoefficientForAllRulesInTable(TableDAO table) throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException
	{
		Set<String> allRules = table.getAllRulesInTable();
		Double[] defectInjectionFrequencyColumn = table.getDefectInjectionFrequencyColumnForRule();
		HashMap<String, Double> ranks= new HashMap<String,Double>();
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
			 ranks.put(rule, coeff);
		 }
		 return ranks;
	}
	
	

	 

}
