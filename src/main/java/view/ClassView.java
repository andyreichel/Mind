package view;

import java.util.ArrayList;
import java.util.List;

import com.hp.gagawa.java.elements.Table;
import com.hp.gagawa.java.elements.Tr;

import dao.ResourceInfoRowDAO;
import dao.StatisticsDAO;
import dao.TableDAO;

public class ClassView {
	public static Table getClassViewTable(TableDAO table)
	{
		List<String> cols = new ArrayList<String>();
		cols.add("Class");
		cols.add("Expextec number of Defects");
		cols.add("Error");
		cols.add("Maintainability");
		cols.add("Principal");
		cols.add("Next Rule");
		
		Table  ruleViewTable = new Table();
		ruleViewTable.appendChild(HTMLBuilder.getHeadRowWithColumns(cols));
		
		for(String version : table.getVersions())
		{
			for(ResourceInfoRowDAO resource : table.getResourceInfoRowsForVersion(version))	
			{
				Tr row = new Tr();
				HTMLBuilder.addColValueToRow(row, resource.getResourceName());
				HTMLBuilder.addColValueToRow(row, "NA"); //Exp n defects
				HTMLBuilder.addColValueToRow(row, "NA"); //Error
				HTMLBuilder.addColValueToRow(row, "NA"); //Maintainablility
				HTMLBuilder.addColValueToRow(row, "NA"); //Principal
				HTMLBuilder.addColValueToRow(row, "NA"); //Next rule
				ruleViewTable.appendChild(row);
			}
		}
		
		return ruleViewTable;
	}
}
