package mind;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;


public class DiffParserTest {
	@Test
	public void getMapOfChangesPerResourceFromDiffOutputTest_oneEntry() throws IOException
	{
		String diffOutput = "--- a/README" + "\n" +
							"+++ b/README"  + "\n" +
							"@@ -2 +2 @@"  + "\n" +
							"-die erste zeile"  + "\n" +
							"+" + "\n" +
							"@@ -4 +4 @@" + "\n" +
							"-die dritte zeile" + "\n" +
							"+die dritte zei" + "\n";

		HashMap<String, Integer> expectedOutput = new HashMap<String, Integer>();
		expectedOutput.put("README", 2);
		Assert.assertEquals(expectedOutput, DiffParser.getMapOfChangesPerResourceFromDiffOutput(diffOutput));
	}
	
	@Test
	public void getMapOfChangesPerResourceFromDiffOutputTest_twoEntry() throws IOException
	{
		String diffOutput = 
				"diff --git a/README b/README" + "\n" +
				"index 5968938..8b5aae8 100644" + "\n" +
				"--- a/README" + "\n" +
				"+++ b/README" + "\n" +
				"@@ -2 +2 @@" + "\n" +
				"-die erste zeile" + "\n" +
				"+" + "\n" +
				"@@ -4 +4 @@" + "\n" +
				"-die dritte zeile" + "\n" +
				"+die dritte zei" + "\n" +
				"diff --git a/src/SOMEOTHERFILE b/src/SOMEOTHERFILE" + "\n" +
				"index dd949e0..ab08c41 100644" + "\n" +
				"--- a/src/SOMEOTHERFILE" + "\n" +
				"+++ b/src/SOMEOTHERFILE" + "\n" +
				"@@ -3 +3 @@" + "\n" +
				"-f" + "\n" +
				"+Hier ist die änderung" + "\n" +
				"@@ -5 +5 @@" + "\n" +
				"-af" + "\n" +
				"+" + "\n" +
				"@@ -7,0 +8 @@" + "\n" +
				"+neu";

		HashMap<String, Integer> expectedOutput = new HashMap<String, Integer>();
		expectedOutput.put("README", 2);
		expectedOutput.put("SOMEOTHERFILE", 3);
		Assert.assertEquals(expectedOutput, DiffParser.getMapOfChangesPerResourceFromDiffOutput(diffOutput));
	}
	
	@Test
	public void getMapOfChangesPerResourceFromDiffOutputTest_noOutput() throws IOException
	{
		String diffOutput = "";

		HashMap<String, Integer> expectedOutput = new HashMap<String, Integer>();
		Assert.assertEquals(expectedOutput, DiffParser.getMapOfChangesPerResourceFromDiffOutput(diffOutput));
	}
}
