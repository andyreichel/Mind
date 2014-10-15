package view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

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
	
	public static void addColValueToRow(Tr row, String value)
	{
		Td col = new Td();
		col.appendChild(new Text(value));
		row.appendChild(col);
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
		List<String> headerCols = new ArrayList<String>();
		headerCols.add("rule");
		headerCols.add("Roh");
		headerCols.add("Average");
		t.appendChild(getHeadRowWithColumns(headerCols));
		Tbody tbody = new Tbody();
		for(String rule : stats.getAverageForAllRules().keySet())
		{
			Tr row = new Tr();
			addColValueToRow(row, rule);
			if(stats.getSpearmanCoefficientForAllRules().get(rule) == null)
			{
				addColValueToRow(row, "null");
			}
			else
			{
				addColValueToRow(row, stats.getSpearmanCoefficientForAllRules().get(rule).toString());
			}
			
			if(stats.getAverageForAllRules().get(rule) == null)
			{
				addColValueToRow(row, "null");
			}
			else
			{
				addColValueToRow(row, stats.getAverageForAllRules().get(rule).toString());
			}
			tbody.appendChild(row);
		}
		
		t.appendChild(tbody);
		statisticsHtml.appendChild(t);
		return statisticsHtml;
	}
	
	public static Thead getHeadRowWithColumns(List<String> cols)
	{
		Thead tableHead = new Thead();
		Tr headerRow = new Tr();
		for(String col : cols)
		{
			Th colTh = new Th();
			colTh.appendChild(new Text(col));
			headerRow.appendChild(colTh);
		}
		tableHead.appendChild(headerRow);
		return tableHead;
	}
	
	public static Table getHtmlTableWithCodeInfo(TableDAO table)
	{
		Table t = new Table();
		t.setAttribute("cellspacing", "\"0\"");
		List<String> headerCols = new ArrayList<String>();
		headerCols.add("Resource");
		headerCols.add("Version");
		headerCols.add("Number of Defects");
		headerCols.add("Size");
		headerCols.add("lines of code touched");
				
		for(String rule : table.getAllRulesInTable())
		{
			headerCols.add(rule);
		}
		t.appendChild(getHeadRowWithColumns(headerCols));

		
		Tbody tbody = new Tbody();
		for(String version : table.getVersions())
		{
			List<ResourceInfoRowDAO> resourceRows= table.getResourceInfoRowsForVersion(version);
			for(ResourceInfoRowDAO resource : resourceRows)
			{
				Tr row = new Tr();
				addColValueToRow(row, resource.getResourceName());
				addColValueToRow(row, version);
				addColValueToRow(row, resource.getNumberDefects().toString());
				addColValueToRow(row, resource.getSize().toString());
				
				if(resource.getLocTouched() == null)
					addColValueToRow(row, "null");
				else
					addColValueToRow(row, resource.getLocTouched().toString());
				
				for(String violation : resource.getViolationsMap().keySet())
				{
					addColValueToRow(row, resource.getViolationsMap().get(violation).toString());
				}
				tbody.appendChild(row);
			}
		}
		t.appendChild(tbody);
		return t;
	}
}
