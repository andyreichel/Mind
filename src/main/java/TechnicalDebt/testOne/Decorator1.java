package TechnicalDebt.testOne;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;

public class Decorator1 implements Decorator {
	private static final Logger LOG = LoggerFactory.getLogger(Decorator1.class);
    public boolean shouldExecuteOnProject(Project project) {
        // Execute only on Java projects
        return true;
    }
 
    public void decorate(Resource resource, DecoratorContext context) {
        // This method is executed on the whole tree of resources.
        // Bottom-up navigation : Java methods -&amp;gt; Java classes -&amp;gt; files -&amp;gt; packages -&amp;gt; modules -&amp;gt; project
        if (Scopes.isBlockUnit(resource)) {
            // Sonar API includes many libraries like commons-lang and google-collections
            double value = RandomUtils.nextDouble();
            // Add a measure to the current Java method
            //context.saveMeasure(ExampleMetrics.RANDOM, value);
            LOG.info("HEEEEY" + context.getViolations().toString());
            
            LOG.info("##########################################################");
            
        } else {
            // we sum random values on resources different than method
            //context.saveMeasure(ExampleMetrics.RANDOM, MeasureUtils.sum(true, context.getChildrenMeasures(ExampleMetrics.RANDOM)));
        }
        PrintWriter writer;
		try {
			writer = new PrintWriter("c:\\the-file-name.txt", "UTF-8");
	        writer.println(context.getMeasure(CoreMetrics.NCLOC).getIntValue());
	        writer.println(context.getMeasure(CoreMetrics.NCLOC).getMetric().toString());
	        writer.println(context.getMeasure(CoreMetrics.VIOLATIONS).getIntValue());
	        //writer.println("The second line " + context.getChildrenMeasures(arg0));
	        writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
    
    @DependsUpon
    public Collection<Metric> dependsOnMetrics() {
      return Arrays.asList(CoreMetrics.FUNCTIONS, CoreMetrics.NCLOC,CoreMetrics.VIOLATIONS);
    }
 
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}