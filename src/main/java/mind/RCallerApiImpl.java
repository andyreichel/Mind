package mind;

import interfaces.MindConfiguration;
import interfaces.RCallerApi;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.ArrayUtils;

import rcaller.RCaller;
import rcaller.RCode;
import rcaller.exception.ParseException;

import com.google.inject.Inject;

import exceptions.AverageCouldNotBeCalculatedException;
import exceptions.LenghtOfDoubleArraysDifferException;
import exceptions.RankCouldNotBeCalculatedException;

public class RCallerApiImpl implements RCallerApi {
	private String RscriptExecutablePath;
	
	@Inject
	RCallerApiImpl(MindConfiguration config) throws ConfigurationException
	{
		RscriptExecutablePath = config.getRscriptExecutablePath();
	}
	
	public Double getSpearmanCoefficient(Double[] column1, Double[] column2) throws LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException {
	    if(column1.length != column2.length)
	    {
	    	throw new LenghtOfDoubleArraysDifferException("column 1 has lenght: " + column1 + "\n"
	    												+ "column 2 has lenght: " + column2);
	    }
		
	    RCaller caller = new RCaller();
	    RCode code = new RCode();
	    /*
	     * Full path of the Rscript. Rscript is an executable file shipped with R.
	     * It is something like C:\\Program File\\R\\bin.... in Windows
	     */
	    caller.setRscriptExecutable(RscriptExecutablePath);
	
	    code.addDoubleArray("x", ArrayUtils.toPrimitive(column1));
	    code.addDoubleArray("y", ArrayUtils.toPrimitive(column2));
	
		String RScript = "cor.s=cor(x,y, use=\"pairwise.complete.obs\", method=\"spearman\")";
		code.addRCode(RScript);
	
		caller.setRCode(code);
		caller.runAndReturnResult("cor.s");
		 
		double[] result;
		try{
			result = caller.getParser().getAsDoubleArray("cor.s");
		}catch(ParseException pe)
		{
			throw new RankCouldNotBeCalculatedException("Spearman correlation coefficient could not be calculated with given values.\n" + pe.getMessage());
		}
			 
		return result[0];
	}

	public Double getMeanOfVector(Double[] vec) throws AverageCouldNotBeCalculatedException {
		RCaller caller = new RCaller();
		caller.setRscriptExecutable(RscriptExecutablePath);
		RCode code = new RCode();
		code.addDoubleArray("x", ArrayUtils.toPrimitive(vec));
		String RScript = "meanRes=mean(x)";
		code.addRCode(RScript);
		caller.setRCode(code);
		
		try
		{
			caller.runAndReturnResult("meanRes");
			return caller.getParser().getAsDoubleArray("meanRes")[0];
		}catch(ParseException pe)
		{
			throw new AverageCouldNotBeCalculatedException("Average of vector could not be calculated with given values.\n");
		}
	}
}
