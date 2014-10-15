package view;

import java.util.ArrayList;
import java.util.List;

import com.hp.gagawa.java.elements.Html;
import com.hp.gagawa.java.elements.Table;
import com.hp.gagawa.java.elements.Td;
import com.hp.gagawa.java.elements.Text;
import com.hp.gagawa.java.elements.Tr;

import dao.StatisticsDAO;

public class RuleView {
	public static Table getRuleViewTable(StatisticsDAO stats)
	{
		List<String> cols = new ArrayList<String>();
		cols.add("Rule");
		cols.add("Rho");
		cols.add("Rank");
		cols.add("Defect / Violations");
		cols.add("Violations");
		cols.add("Effort fixing one");
		cols.add("Effort fixing all");
		
		Table  ruleViewTable = new Table();
		ruleViewTable.appendChild(HTMLBuilder.getHeadRowWithColumns(cols));
		
		for(String rule : stats.getSpearmanCoefficientForAllRules().keySet())
		{
			Tr row = new Tr();
			HTMLBuilder.addColValueToRow(row, rule);
			
			if(stats.getSpearmanCoefficientForAllRules().get(rule) == null)
				HTMLBuilder.addColValueToRow(row, "NA");
			else 
				HTMLBuilder.addColValueToRow(row, stats.getSpearmanCoefficientForAllRules().get(rule).toString());
			
			HTMLBuilder.addColValueToRow(row, stats.getRankOfRules().get(rule).toString());//RANK
			
			if(stats.getAverageForAllRules().get(rule) == null)
				HTMLBuilder.addColValueToRow(row, "NA");
			else 
				HTMLBuilder.addColValueToRow(row, stats.getAverageForAllRules().get(rule).toString());
			
			HTMLBuilder.addColValueToRow(row, stats.getNumberOfViolationsThroughoutAllVersions(rule).toString());//VIOLATIONS
			HTMLBuilder.addColValueToRow(row, "to be developed");//EFFORT for 1 
			HTMLBuilder.addColValueToRow(row, "to be developed");//EFFORT for all
			ruleViewTable.appendChild(row);
		}
		
		return ruleViewTable;
	}
	
}
