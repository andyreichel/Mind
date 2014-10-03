package mind;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.junit.Assert;
import org.junit.Test;


public class TestTableFilter {
	@Test
	public void test_filterTable_successfull()
	{
		HashMap<String, Integer> class1v1_row = new HashMap<String, Integer>();
		class1v1_row.put("numberDefects", 1);
		class1v1_row.put("locTouched", null);
		class1v1_row.put("size", 0);
		class1v1_row.put("r1", 0);
		class1v1_row.put("r2", 0);
		
		
		HashMap<String, Integer> class1v2_row = new HashMap<String, Integer>();
		class1v2_row.put("numberDefects", 1);
		class1v2_row.put("locTouched", 150);
		class1v2_row.put("size", 500);
		class1v2_row.put("r1", 1);
		class1v2_row.put("r2", 0);
		
		HashMap<String, Integer> class1v3_row = new HashMap<String, Integer>();
		class1v3_row.put("numberDefects", 1);
		class1v3_row.put("locTouched", 50);
		class1v3_row.put("size", 550);
		class1v3_row.put("r1", 5);
		class1v3_row.put("r2", 7);
		
		HashMap<String, Integer> class2v1_row = new HashMap<String, Integer>();
		class2v1_row.put("numberDefects", 3);
		class2v1_row.put("locTouched", null);
		class2v1_row.put("size", 0);
		class2v1_row.put("r1", 0);
		class2v1_row.put("r2", 0);


		HashMap<String, Integer> class2v2_row = new HashMap<String, Integer>();
		class2v2_row.put("numberDefects", 2);
		class2v2_row.put("locTouched", 150);
		class2v2_row.put("size", 500);
		class2v2_row.put("r1", 13);
		class2v2_row.put("r2", 9);
		
		HashMap<String, Integer> class2v3_row = new HashMap<String, Integer>();
		class2v3_row.put("numberDefects", 2);
		class2v3_row.put("locTouched", 25);
		class2v3_row.put("size", 550);
		class2v3_row.put("r1", 0);
		class2v3_row.put("r2", 7);
		
		HashMap<String, Integer> class3v3_row = new HashMap<String, Integer>();
		class3v3_row.put("numberDefects", 0);
		class3v3_row.put("locTouched", null);
		class3v3_row.put("size", 0);
		class3v3_row.put("r1", 0);
		class3v3_row.put("r2", 0);
		
		
		HashMap<String, HashMap<String, Integer>> v1rows = new HashMap<String, HashMap<String,Integer>>();
		HashMap<String, HashMap<String, Integer>> v2rows = new HashMap<String, HashMap<String,Integer>>();
		HashMap<String, HashMap<String, Integer>> v3rows = new HashMap<String, HashMap<String,Integer>>();
		v1rows.put("class1", class1v1_row);
		v1rows.put("class2", class2v1_row);
		v2rows.put("class1", class1v2_row);
		v2rows.put("class2", class2v2_row);
		v3rows.put("class1", class1v3_row);
		v3rows.put("class2", class2v3_row);
		v3rows.put("class3", class3v3_row);
		
		LinkedHashMap<String, HashMap<String, HashMap<String, Integer>>> toBeFilteredTable = new LinkedHashMap<String, HashMap<String, HashMap<String, Integer>>>();
		toBeFilteredTable.put("v1", v1rows);
		toBeFilteredTable.put("v2", v2rows);
		toBeFilteredTable.put("v3", v3rows);
		
		LinkedHashMap<String, HashMap<String, HashMap<String, Integer>>> expectedTable = new LinkedHashMap<String, HashMap<String, HashMap<String, Integer>>>();
		HashMap<String, Integer> expectedClass1v2_row = new HashMap<String, Integer>();
		expectedClass1v2_row.put("numberDefects", 1);
		expectedClass1v2_row.put("locTouched", 150);
		expectedClass1v2_row.put("size", 500);
		expectedClass1v2_row.put("r1", 1);
		expectedClass1v2_row.put("r2", 0);
		
		HashMap<String, Integer> expectedClass1v3_row = new HashMap<String, Integer>();
		expectedClass1v3_row.put("numberDefects", 1);
		expectedClass1v3_row.put("locTouched", 50);
		expectedClass1v3_row.put("size", 550);
		expectedClass1v3_row.put("r1", 5);
		expectedClass1v3_row.put("r2", 7);

		HashMap<String, Integer> expectedClass2v2_row = new HashMap<String, Integer>();
		expectedClass2v2_row.put("numberDefects", 2);
		expectedClass2v2_row.put("locTouched", 150);
		expectedClass2v2_row.put("size", 500);
		expectedClass2v2_row.put("r1", 13);
		expectedClass2v2_row.put("r2", 9);
		
		HashMap<String, Integer> expectedClass2v3_row = new HashMap<String, Integer>();
		expectedClass2v3_row.put("numberDefects", 2);
		expectedClass2v3_row.put("locTouched", 25);
		expectedClass2v3_row.put("size", 550);
		expectedClass2v3_row.put("r1", 0);
		expectedClass2v3_row.put("r2", 7);
		
		
		HashMap<String, HashMap<String, Integer>> expectedV2rows = new HashMap<String, HashMap<String,Integer>>();
		HashMap<String, HashMap<String, Integer>> expectedV3rows = new HashMap<String, HashMap<String,Integer>>();
		expectedV2rows.put("class1", expectedClass1v2_row);
		expectedV2rows.put("class2", expectedClass2v2_row);
		expectedV3rows.put("class1", expectedClass1v3_row);
		expectedV3rows.put("class2", expectedClass2v3_row);
		expectedTable.put("v2", expectedV2rows);
		expectedTable.put("v3", expectedV3rows);
		
		TableFilter.filterTable(toBeFilteredTable);
		Assert.assertEquals(expectedTable, toBeFilteredTable);
		
	}
}
