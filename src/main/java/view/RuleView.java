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
		cols.add("DefectInjectionFrequency");
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
			HTMLBuilder.addColValueToRow(row, "NA");//RANK
			HTMLBuilder.addColValueToRow(row, "NA");// DEFECT/VIOLATION
			HTMLBuilder.addColValueToRow(row, "NA");//VIOLATIONS
			HTMLBuilder.addColValueToRow(row, "NA");//EFFORT for 1 
			HTMLBuilder.addColValueToRow(row, "NA");//EFFORT for all
			ruleViewTable.appendChild(row);
		}
		
		return ruleViewTable;
	}
	
}
