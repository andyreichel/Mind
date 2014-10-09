package interfaces;

import java.util.HashMap;

import dao.TableDAO;
import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.NoTableSetForCalculatingStatsException;
import exceptions.PropertyNotFoundException;

public interface StatisticGenerator  {
	public  HashMap<String, Double> getSpearmanCoefficientForAllRulesInTable() throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException, NoTableSetForCalculatingStatsException;
	public HashMap<String, Double> getAverageViolationsForAllRulesInTable(TableDAO table) throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException;
	public Double[] getDefectInjectionFrequencyColumn() throws PropertyNotFoundException;
	public Double[] getViolationDensityDencityColumnForRule(String rule) throws PropertyNotFoundException;
	public void setTableDAO(TableDAO table);
}
