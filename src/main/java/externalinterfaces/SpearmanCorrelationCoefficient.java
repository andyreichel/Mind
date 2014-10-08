package externalinterfaces;

import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.RankCouldNotBeCalculatedException;

public interface SpearmanCorrelationCoefficient {
	Double getCoefficient(Double[] column1, Double[] column2) throws LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException;
}

