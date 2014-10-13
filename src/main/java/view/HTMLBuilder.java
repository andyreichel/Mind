package view;

import java.util.List;

import com.hp.gagawa.java.elements.Body;
import com.hp.gagawa.java.elements.H1;
import com.hp.gagawa.java.elements.Head;
import com.hp.gagawa.java.elements.Html;
import com.hp.gagawa.java.elements.Link;
import com.hp.gagawa.java.elements.Table;
import com.hp.gagawa.java.elements.Tbody;
import com.hp.gagawa.java.elements.Td;
import com.hp.gagawa.java.elements.Text;
import com.hp.gagawa.java.elements.Th;
import com.hp.gagawa.java.elements.Thead;
import com.hp.gagawa.java.elements.Title;
import com.hp.gagawa.java.elements.Tr;

import dao.ResourceInfoRowDAO;
import dao.StatisticsDAO;
import dao.TableDAO;

public class HTMLBuilder {
	public static Head getHtmlHeader()
	{
		Head head = new Head();
		Title title = new Title();
		title.appendChild(new Text("MIND"));
		head.appendChild(title);
		Link css = new Link();
		css.setHref("./CSS/style.css");
		css.setRel("stylesheet");
		css.setType("text/css");
		head.appendChild(css);
		return head;
	}
	
	public static String getHtmlPage(TableDAO table, StatisticsDAO stats)
	{
		Html html = new Html();
		html.appendChild(getHtmlHeader());
		Body body = new Body();
		html.appendChild(body);
		H1 h1 = new H1();
		h1.appendChild(new Text("Table with information."));
		body.appendChild(h1);
		body.appendChild(getHtmlTableWithCodeInfo(table));
		H1 statisticsHeadline = new H1();
		statisticsHeadline.appendChild(new Text("Statistics"));
		body.appendChild(statisticsHeadline);
		body.appendChild(getHtmlStatistics(stats));
		return html.write();
	}
	
	public static Html getHtmlStatistics(StatisticsDAO stats)
	{
		Html statisticsHtml = new Html();
		statisticsHtml.appendChild(new Text("P-value of analyze is: " + stats.getpValue()));
		Table t = new Table();
		t.setAttribute("cellspacing", "\"0\"");
		Tr headerRow = new Tr();
		Thead thead = new Thead();
		Th spearmanRoh = new Th();
		spearmanRoh.appendChild(new Text("Roh"));
		headerRow.appendChild(spearmanRoh);
		Th average = new Th();
		average.appendChild(new Text("Average"));
		headerRow.appendChild(average);
		thead.appendChild(headerRow);
		Tbody tbody = new Tbody();
		for(String rule : stats.getAverageForAllRules().keySet())
		{
			Tr row = new Tr();
			Td ruleCol = new Td();
			ruleCol.appendChild(new Text(rule));
			row.appendChild(ruleCol);
			Td spearmanCol = new Td();
			if(stats.getSpearmanCoefficientForAllRules().get(rule) == null)
				spearmanCol.appendChild(new Text("null"));
			else
				spearmanCol.appendChild(new Text(stats.getSpearmanCoefficientForAllRules().get(rule)));
			
			row.appendChild(spearmanCol);
			Td averageCol = new Td();
			
			if(stats.getAverageForAllRules().get(rule) == null)
				averageCol.appendChild(new Text("null"));
			else
				averageCol.appendChild(new Text(stats.getAverageForAllRules().get(rule)));
			
			row.appendChild(averageCol);
			
			tbody.appendChild(row);
		}
		t.appendChild(thead);
		t.appendChild(tbody);
		statisticsHtml.appendChild(t);
		return statisticsHtml;
	}
	
	public static Table getHtmlTableWithCodeInfo(TableDAO table)
	{
		Table t = new Table();
		t.setAttribute("cellspacing", "\"0\"");
		Tr headerRow = new Tr();
		Thead thead = new Thead();
		Th resourceNameHeaderCol = new Th();
		resourceNameHeaderCol.appendChild(new Text("Resource"));
		headerRow.appendChild(resourceNameHeaderCol);
		Th versionHeaderCol = new Th();
		versionHeaderCol.appendChild(new Text("Version"));
		headerRow.appendChild(versionHeaderCol);
		Th numberDefects = new Th();
		numberDefects.appendChild(new Text("Number of Defects"));
		headerRow.appendChild(numberDefects);
		Th size = new Th();
		size.appendChild(new Text("Size"));
		headerRow.appendChild(size);
		Th locTouched = new Th();
		locTouched.appendChild(new Text("lines of code touched"));
		headerRow.appendChild(locTouched);
		
		for(String rule : table.getAllRulesInTable())
		{
			Th headerColumn = new Th();
			headerColumn.appendChild(new Text(rule));
			headerRow.appendChild(headerColumn);
		}
		thead.appendChild(headerRow);
		t.appendChild(thead);
		
		Tbody tbody = new Tbody();
		for(String version : table.getVersions())
		{
			List<ResourceInfoRowDAO> resourceRows= table.getResourceInfoRowsForVersion(version);
			for(ResourceInfoRowDAO resource : resourceRows)
			{
				Tr row = new Tr();
				Td resourceCol = new Td();
				resourceCol.appendChild(new Text(resource.getResourceName()));
				row.appendChild(resourceCol);
				Td versionCol = new Td();
				versionCol.appendChild(new Text(version));
				row.appendChild(versionCol);
				
				Td defectCol = new Td();
				defectCol.appendChild(new Text(resource.getNumberDefects()));
				row.appendChild(defectCol);
				
				Td sizeCol = new Td();
				sizeCol.appendChild(new Text(resource.getSize()));
				row.appendChild(sizeCol);
				
				Td locTouchedCol = new Td();
				if(resource.getLocTouched() == null)
					locTouchedCol.appendChild(new Text("null"));
				else
					locTouchedCol.appendChild(new Text(resource.getLocTouched()));
				row.appendChild(locTouchedCol);
				
				for(String violation : resource.getViolationsMap().keySet())
				{
					Td violationCol = new Td();
					violationCol.appendChild(new Text(resource.getViolationsMap().get(violation)));
					row.appendChild(violationCol);
				}
				tbody.appendChild(row);
			}
		}
		t.appendChild(tbody);
		return t;
	}
}
