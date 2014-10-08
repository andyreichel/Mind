package interfaces;

import java.util.HashMap;

import dao.TableDAO;
import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.PropertyNotFoundException;

public interface StatisticGenerator  {
	public  HashMap<String, Double> getSpearmanCoefficientForAllRulesInTable(TableDAO table) throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException;
}
