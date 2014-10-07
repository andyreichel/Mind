package mind;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.ArrayUtils;

import rcaller.RCaller;
import rcaller.RCode;
import rcaller.exception.ParseException;

public class SpearmanCorrelationCoefficientImpl implements SpearmanCorrelationCoefficient{
	private String RscriptExecutablePath;
	
	SpearmanCorrelationCoefficientImpl(Configuration config)
	{
		RscriptExecutablePath = config.getString("R.RscriptExecutablePath");
		
	}
	
	public Double getCoefficient(Double[] column1, Double[] column2) throws LenghtOfDoubleArraysDifferException, RankCouldNotBeCalculatedException {
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

}
