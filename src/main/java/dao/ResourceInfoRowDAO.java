package dao;

import java.util.HashMap;
import java.util.Set;

import exceptions.PropertyNotFoundException;


/**
 * 
 * Class that stores the information for one row of a resource of one version for example
 * {name: "class1", locTouched: 50, size: 100, numberDefects: 2, violationsPerRule : {"rule1": 5, "rule2": 7}}
 *
 */
public class ResourceInfoRowDAO {
	/**
	 * Size of the resource
	 */
	private Integer size;
	/**
	 * lines of code that changed since the last version
	 */
	private Integer locTouched;
	/**
	 * number of defects that reference to this class in this revision
	 */
	private Integer numberDefects;
	/**
	 * map of number of violations per rule
	 */
	private HashMap<String, Integer> violationsPerRule;
	private String resourceName;
	
	public ResourceInfoRowDAO(String resourceName)
	{
		this.resourceName = resourceName;
	}
	
	public void setSize(Integer size)
	{
		this.size = size;
	}
	
	public void setLocTouched(Integer locTouched)
	{
		this.locTouched = locTouched;
	}
	
	public void setNumberOfDefects(Integer numberOfDefects)
	{
		this.numberDefects = numberOfDefects;
	}
	
	public void setViolationsPerRule(HashMap<String, Integer> violationsPerRule)
	{
		this.violationsPerRule = violationsPerRule;
	}
	
	public Integer getLocTouched()
	{
		return locTouched;
	}
	
	public Integer getSize()
	{
		return size;
	}
	
	public Integer getNumberDefects()
	{
		return numberDefects;
	}
	
	public String getResourceName()
	{
		return resourceName;
	}
	
	public Set<String> getAllRuleKeys()
	{
		return violationsPerRule.keySet();
	}
	
	public Integer getNumberOfViolationsForRule(String rule) throws PropertyNotFoundException
	{
		if(!violationsPerRule.containsKey(rule))
			throw new PropertyNotFoundException(rule + " not found in rules");
		return violationsPerRule.get(rule);
	}
	
	public HashMap<String, Integer> getViolationsMap()
	{
		return violationsPerRule;
	}
	
	public boolean equals(Object obj)
	{
		if(obj == this)
			return true;
		
		if(!(obj instanceof ResourceInfoRowDAO))
			return false;
		
		ResourceInfoRowDAO object = (ResourceInfoRowDAO) obj;
		
		return 	areObjectsEqual(this.numberDefects, object.getNumberDefects()) &&
				areObjectsEqual(this.locTouched, object.getLocTouched()) &&
				areObjectsEqual(this.size, object.getSize()) &&
				areObjectsEqual(this.resourceName, object.getResourceName()) &&
				areObjectsEqual(this.violationsPerRule, object.getViolationsMap());
		
	}
	
	private boolean areObjectsEqual(Object one, Object two)
	{
		if(one!=null)
		{
			return one.equals(two);
		}else return two==null;
	}
	
	public int hashCode() {
		int result = 17;
		result = 31 * result + size ;
		result = 31 * result + locTouched;
		result = 31 * result + numberDefects;
		result = 31 * result + resourceName.hashCode();
		result = 31 * result + violationsPerRule.hashCode();
		return result;
		}
	
	public String toString()
	{
		StringBuilder rowString = new StringBuilder();
		rowString.append(resourceName);
		rowString.append("\n");
		rowString.append("Lines of code touched: " + locTouched);
		rowString.append("\tnumber of defects: " + numberDefects);
		rowString.append("\tsize of resource: " + size);
		for(String rule : violationsPerRule.keySet())
		{
			rowString.append("\t\t" + rule+ ": " + violationsPerRule.get(rule));	
		}
		return rowString.toString();
	}
}
