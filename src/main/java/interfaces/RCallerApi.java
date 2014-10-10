package interfaces;

import java.util.List;

import exceptions.AverageCouldNotBeCalculatedException;
import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.PValueCouldNotBeCalculatedException;
import exceptions.RankCouldNotBeCalculatedException;

public interface RCallerApi {
	public Double getSpearmanCoefficient(Double[] column1, Double[] column2) throws LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException;
	public Double getMeanOfVector(Double[] vec) throws AverageCouldNotBeCalculatedException;
	public Double getPvalue(double[][] vec) throws PValueCouldNotBeCalculatedException;
}
