package interfaces;

import java.util.List;

import dao.TableDAO;
import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.PropertyNotFoundException;
import exceptions.RankCouldNotBeCalculatedException;

public interface StatisticGenerator  {
	public List<Double> getSpearmanCoefficientForAllRulesInTable(TableDAO table) throws PropertyNotFoundException, LenghtOfDoubleArraysDifferException;
}
