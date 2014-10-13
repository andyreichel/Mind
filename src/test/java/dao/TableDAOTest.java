package dao;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import testutils.TestUtils;

import com.google.common.collect.ImmutableMap;


public class TableDAOTest {
	@Test
	public void test_filterTable_successfull()
	{
		ResourceInfoRowDAO class1v1_row = TestUtils.getResourceInfoRow("class1", 0, null, 0, ImmutableMap.of("r1",0, "r2", 0));
		ResourceInfoRowDAO class1v2_row = TestUtils.getResourceInfoRow("class1", 1, 150, 500, ImmutableMap.of("r1",1, "r2", 0));
		ResourceInfoRowDAO class1v3_row = TestUtils.getResourceInfoRow("class1", 1, 0, 550, ImmutableMap.of("r1",5, "r2", 7));
		ResourceInfoRowDAO class2v1_row = TestUtils.getResourceInfoRow("class2", 3, null, 0, ImmutableMap.of("r1",0, "r2", 0));
		ResourceInfoRowDAO class2v2_row = TestUtils.getResourceInfoRow("class2", 2, 150, 500, ImmutableMap.of("r1",13, "r2", 9));
		ResourceInfoRowDAO class2v3_row = TestUtils.getResourceInfoRow("class2", 2, 25, 550, ImmutableMap.of("r1",0, "r2", 7));
		ResourceInfoRowDAO class3v3_row = TestUtils.getResourceInfoRow("class3", 0, null, 0, ImmutableMap.of("r1",0, "r2", 0));
		
		List<ResourceInfoRowDAO> v1rows = new ArrayList<ResourceInfoRowDAO>();
		List<ResourceInfoRowDAO> v2rows = new ArrayList<ResourceInfoRowDAO>();
		List<ResourceInfoRowDAO> v3rows = new ArrayList<ResourceInfoRowDAO>();
		v1rows.add(class1v1_row);
		v1rows.add(class2v1_row);
		v2rows.add(class1v2_row);
		v2rows.add(class2v2_row);
		v3rows.add(class1v3_row);
		v3rows.add(class2v3_row);
		v3rows.add(class3v3_row);
		
		LinkedHashMap<String, List<ResourceInfoRowDAO>> toBeFilteredTable = new LinkedHashMap<String, List<ResourceInfoRowDAO>>();
		toBeFilteredTable.put("v1", v1rows);
		toBeFilteredTable.put("v2", v2rows);
		toBeFilteredTable.put("v3", v3rows);
		
		LinkedHashMap<String, List<ResourceInfoRowDAO>> expectedTable = new LinkedHashMap<String, List<ResourceInfoRowDAO>>();
		
		ResourceInfoRowDAO expectedClass1v2_row = TestUtils.getResourceInfoRow("class1", 1, 150, 500, ImmutableMap.of("r1",1, "r2", 0));
		ResourceInfoRowDAO expectedClass2v2_row = TestUtils.getResourceInfoRow("class2", 2, 150, 500, ImmutableMap.of("r1",13, "r2", 9));
		ResourceInfoRowDAO expectedClass2v3_row = TestUtils.getResourceInfoRow("class2", 2, 25, 550, ImmutableMap.of("r1",0, "r2", 7));
		

		List<ResourceInfoRowDAO> expectedV2rows = new ArrayList<ResourceInfoRowDAO>();
		expectedV2rows.add(expectedClass1v2_row);
		expectedV2rows.add(expectedClass2v2_row);
		
		List<ResourceInfoRowDAO> expectedV3rows = new ArrayList<ResourceInfoRowDAO>();
		expectedV3rows.add(expectedClass2v3_row);
		
		expectedTable.put("v2", expectedV2rows);
		expectedTable.put("v3", expectedV3rows);
		
		TableDAO tableDAO = new TableDAO(toBeFilteredTable);
		tableDAO.filterTable();
		
		TableDAO expectedTableDAO = new TableDAO(expectedTable);
		Assert.assertEquals(expectedTableDAO, tableDAO);
	}
	
	@Test
	public void test_filterTable_emptyTable()
	{
		
		LinkedHashMap<String, List<ResourceInfoRowDAO>> toBeFilteredTable = new LinkedHashMap<String, List<ResourceInfoRowDAO>>();
		LinkedHashMap<String, List<ResourceInfoRowDAO>> expectedTable = new LinkedHashMap<String, List<ResourceInfoRowDAO>>();
		
		TableDAO table = new TableDAO(toBeFilteredTable);
		table.filterTable();
		Assert.assertEquals(expectedTable, toBeFilteredTable);
	}
	
	
	@Test
	public void test_getAllRulesInTable()
	{
		ResourceInfoRowDAO class1v1row = TestUtils.getResourceInfoRow("class1", 1, 13, 5, ImmutableMap.of("r1",5, "r2", 2));
		ResourceInfoRowDAO class2v1row = TestUtils.getResourceInfoRow("class2", 3, 8, 2, ImmutableMap.of("r1",4, "r2", 8));
		
		
		List<ResourceInfoRowDAO> v1Rows = new ArrayList<ResourceInfoRowDAO>();
		v1Rows.add(class1v1row);
		v1Rows.add(class2v1row);
		
		LinkedHashMap<String, List<ResourceInfoRowDAO>> toBeFilteredTable = new LinkedHashMap<String, List<ResourceInfoRowDAO>>();
		toBeFilteredTable.put("v1", v1Rows);
		
		TableDAO table = new TableDAO(toBeFilteredTable);
		
		Set<String> expectedAllRulesList = new HashSet<String>();
		expectedAllRulesList.add("r1");
		expectedAllRulesList.add("r2");
		Assert.assertEquals(expectedAllRulesList, table.getAllRulesInTable());
	}
	
	@Test
	public void test_getAllRulesInTable_emptyTable()
	{
		LinkedHashMap<String, List<ResourceInfoRowDAO>> toBeFilteredTable = new LinkedHashMap<String, List<ResourceInfoRowDAO>>();
		
		TableDAO table = new TableDAO(toBeFilteredTable);
		Set<String> expectedAllRulesList = new HashSet<String>();
		Assert.assertEquals(expectedAllRulesList, table.getAllRulesInTable());
	}
	
	@Test
	public void test_getAllRulesInTable_firstRowHasNoResource()
	{
		LinkedHashMap<String, List<ResourceInfoRowDAO>> toBeFilteredTable = new LinkedHashMap<String, List<ResourceInfoRowDAO>>();
		List<ResourceInfoRowDAO> v1rows = new ArrayList<ResourceInfoRowDAO>();
		toBeFilteredTable.put("v1", v1rows);
		
		TableDAO table = new TableDAO(toBeFilteredTable);
		Set<String> expectedAllRulesList = new HashSet<String>();
		Assert.assertEquals(expectedAllRulesList, table.getAllRulesInTable());
	}
	
	@Test
	public void test_equals_twoEmptyTables()
	{
		LinkedHashMap<String, List<ResourceInfoRowDAO>> tableOne = new LinkedHashMap<String, List<ResourceInfoRowDAO>>();
		LinkedHashMap<String, List<ResourceInfoRowDAO>> tableTwo = new LinkedHashMap<String, List<ResourceInfoRowDAO>>();
		
		Assert.assertEquals(tableOne, tableTwo);
	}
	
	@Test
	public void test_equals_twoSameTables()
	{
		LinkedHashMap<String, List<ResourceInfoRowDAO>> tableOne = new LinkedHashMap<String, List<ResourceInfoRowDAO>>();
		LinkedHashMap<String, List<ResourceInfoRowDAO>> tableTwo = new LinkedHashMap<String, List<ResourceInfoRowDAO>>();
		
		ResourceInfoRowDAO t1class1v1row = TestUtils.getResourceInfoRow("class1", 1, 13, 5, ImmutableMap.of("r1",5, "r2", 2));
		ResourceInfoRowDAO t1class1v2row = TestUtils.getResourceInfoRow("class1", 1, 13, 5, ImmutableMap.of("r1",5, "r2", 3));
		ResourceInfoRowDAO t1class2v1row = TestUtils.getResourceInfoRow("class2", 1, 13, 5, ImmutableMap.of("r1",7, "r2", 2));
		ResourceInfoRowDAO t2class1v1row = TestUtils.getResourceInfoRow("class1", 1, 13, 5, ImmutableMap.of("r1",5, "r2", 2));
		ResourceInfoRowDAO t2class1v2row = TestUtils.getResourceInfoRow("class1", 1, 13, 5, ImmutableMap.of("r1",5, "r2", 3));
		ResourceInfoRowDAO t2class2v1row = TestUtils.getResourceInfoRow("class2", 1, 13, 5, ImmutableMap.of("r1",7, "r2", 2));
		
		
		List<ResourceInfoRowDAO> t1v1Rows = new ArrayList<ResourceInfoRowDAO>();
		t1v1Rows.add(t1class1v1row);
		t1v1Rows.add(t1class2v1row);
		List<ResourceInfoRowDAO> t1v2Rows = new ArrayList<ResourceInfoRowDAO>();
		t1v2Rows.add(t1class1v2row);
		List<ResourceInfoRowDAO> t2v1Rows = new ArrayList<ResourceInfoRowDAO>();
		t2v1Rows.add(t2class1v1row);
		t2v1Rows.add(t2class2v1row);
		List<ResourceInfoRowDAO> t2v2Rows = new ArrayList<ResourceInfoRowDAO>();
		t2v2Rows.add(t2class1v2row);
		tableOne.put("v1", t1v1Rows);
		tableTwo.put("v1", t2v1Rows);
		tableOne.put("v2", t1v2Rows);
		tableTwo.put("v2", t2v2Rows);
		
		TableDAO tableOneDAO = new TableDAO(tableOne);
		TableDAO tableTwoDAO = new TableDAO(tableTwo);
		Assert.assertEquals(tableOneDAO, tableTwoDAO);
	}
	
	@Test
	public void test_equals_twoNotEqualTables()
	{
		LinkedHashMap<String, List<ResourceInfoRowDAO>> tableOne = new LinkedHashMap<String, List<ResourceInfoRowDAO>>();
		LinkedHashMap<String, List<ResourceInfoRowDAO>> tableTwo = new LinkedHashMap<String, List<ResourceInfoRowDAO>>();
		
		ResourceInfoRowDAO t1class1v1row = TestUtils.getResourceInfoRow("class2", 1, 13, 5, ImmutableMap.of("r1",5, "r2", 2));
		ResourceInfoRowDAO t2class1v1row = TestUtils.getResourceInfoRow("class1", 1, 13, 5, ImmutableMap.of("r1",5, "r2", 2));
		
		List<ResourceInfoRowDAO> t1v1Rows = new ArrayList<ResourceInfoRowDAO>();
		t1v1Rows.add(t1class1v1row);
		List<ResourceInfoRowDAO> t2v1Rows = new ArrayList<ResourceInfoRowDAO>();
		t2v1Rows.add(t2class1v1row);
		
		tableOne.put("v1", t1v1Rows);
		tableTwo.put("v1", t2v1Rows);
		
		TableDAO tableOneDAO = new TableDAO(tableOne);
		TableDAO tableTwoDAO = new TableDAO(tableTwo);
		
		Assert.assertNotEquals(tableOneDAO, tableTwoDAO);
	}
	
	@Test
	public void test_equals_twoNotEqualTables_violationsNotEqual()
	{
		LinkedHashMap<String, List<ResourceInfoRowDAO>> tableOne = new LinkedHashMap<String, List<ResourceInfoRowDAO>>();
		LinkedHashMap<String, List<ResourceInfoRowDAO>> tableTwo = new LinkedHashMap<String, List<ResourceInfoRowDAO>>();
		
		ResourceInfoRowDAO t1class1v1row = TestUtils.getResourceInfoRow("class1", 1, 13, 5, ImmutableMap.of("r1",7, "r2", 2));
		ResourceInfoRowDAO t2class1v1row = TestUtils.getResourceInfoRow("class1", 1, 13, 5, ImmutableMap.of("r1",5, "r2", 4));
		
		List<ResourceInfoRowDAO> t1v1Rows = new ArrayList<ResourceInfoRowDAO>();
		t1v1Rows.add(t1class1v1row);
		List<ResourceInfoRowDAO> t2v1Rows = new ArrayList<ResourceInfoRowDAO>();
		t2v1Rows.add(t2class1v1row);
		
		tableOne.put("v1", t1v1Rows);
		tableTwo.put("v1", t2v1Rows);
		
		TableDAO tableOneDAO = new TableDAO(tableOne);
		TableDAO tableTwoDAO = new TableDAO(tableTwo);
		
		Assert.assertNotEquals(tableOneDAO, tableTwoDAO);
	}
}
