package interfaces;

import java.util.HashMap;

import dao.TableDAO;
import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.NoTableSetForCalculatingStatsException;
import exceptions.PValueCouldNotBeCalculatedException;
import exceptions.PropertyNotFoundException;

public interface StatisticGenerator  {
	public  HashMap<String, Double> getSpearmanCoefficientForAllRulesInTable() throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException;
	public HashMap<String, Double> getAverageViolationsForAllRulesInTable() throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException;
	public Double[] getDefectInjectionFrequencyColumn() throws PropertyNotFoundException, NoTableSetForCalculatingStatsException;
	public Double[] getViolationDensityColumnForRule(String rule) throws PropertyNotFoundException, NoTableSetForCalculatingStatsException;
	public void setTableDAO(TableDAO table);
	public Double getPvalue() throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException, PValueCouldNotBeCalculatedException;
}
