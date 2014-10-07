package mind;

import rcaller.RCaller;
import rcaller.RCode;

public class SpearmanCorrelationCoefficientImpl implements SpearmanCorrelationCoefficient{

	public double[] getCoefficient(double[] column1, double[] column2) {
        /*
         * Creating RCaller
         */
        RCaller caller = new RCaller();
        RCode code = new RCode();
        /*
         * Full path of the Rscript. Rscript is an executable file shipped with R.
         * It is something like C:\\Program File\\R\\bin.... in Windows
         */
        caller.setRscriptExecutable("C:\\Program Files\\R\\R-3.1.0\\bin\\Rscript.exe");


//        code.addDoubleArray("x", data);

        /*
         * Adding R Code
         */
        
		  String RScript = "n = c(2, 3, 5)\n"+
		 "s = c(10,20,3)\n"+
		 "cor.s=cor(n,s, use=\"pairwise.complete.obs\", method=\"spearman\")";
		 code.addRCode("n = c(2, 3, 5)");
		 code.addRCode("s = c(10,20,3)");
		 code.addRCode("cor.s=cor(n,s, use=\"pairwise.complete.obs\", method=\"spearman\")");

		 /*
		  * We want to handle the list 'my.all'
		  */
		 caller.setRCode(code);
		 caller.runAndReturnResult("cor.s");
		 
		 double[] results;

		 /*
		  * Retrieving the 'mean' element of list 'my.all'
		  */
		 results = caller.getParser().getAsDoubleArray("cor.s");
		 System.out.println("Mean is " + results[0]);


		 /*
		  * Now we are retrieving the standardized form of vector x
		  */
		 System.out.println("Standardized x is ");
		 
		 for (int i = 0; i < results.length; i++) {
		   System.out.print(results[i] + ", ");
		 }
		return null;
	}

}
