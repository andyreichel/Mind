package mind;

import java.io.IOException;
import java.util.List;

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
import exceptions.PValueCouldNotBeCalculatedException;
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
	
	public Double getPvalue(double[][] mat) throws PValueCouldNotBeCalculatedException
	{
		RCaller caller = new RCaller();
		caller.setRscriptExecutable(RscriptExecutablePath);
		RCode code = new RCode();
		
		
		addMatrixToRCode(mat, code, "li");
		code.addRCode("li");
		code.addRCode("res=kruskal.test(li)");
		code.addRCode("res$p.value");
		caller.setRCode(code);
		
		try
		{
			caller.redirectROutputToConsole();
			caller.runAndReturnResult("res$p.value");
			return caller.getParser().getAsDoubleArray("res$p.value")[0];
		}catch(ParseException pe)
		{
			pe.printStackTrace();
			throw new PValueCouldNotBeCalculatedException("Pvalue of distribution could not be calcultated with given data.\n");
		}
	}
	
	private void addMatrixToRCode(double[][] mat, RCode code, String nameInR) {
		
		int sizeOfMatrix = 0;
		for(int i = 0 ; i < mat.length; i++)
		{
			if(mat[i].length != 0)
			{
				code.addDoubleArray(nameInR +"."+ sizeOfMatrix, mat[i]);
				sizeOfMatrix++;
			}
		}
		
		StringBuilder list = new StringBuilder();
		list.append("list(");
		for(int i = 0; i < sizeOfMatrix; i++)
		{
			list.append("c(li." +i + ")");
			if(i+1 < sizeOfMatrix)
				list.append(",");
		}
		list.append(")");
		code.addRCode("li=" + list.toString());
	}
}
