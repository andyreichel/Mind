package mind;

public interface SpearmanCorrelationCoefficient {
	Double getCoefficient(Double[] column1, Double[] column2) throws LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException;
}

