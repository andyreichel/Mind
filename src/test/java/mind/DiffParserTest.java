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
	public void getMapOfChangesPerResourceFromDiffOutputTest_noOutput() throws IOException
	{
		String diffOutput = "";

		HashMap<String, Integer> expectedOutput = new HashMap<String, Integer>();
		Assert.assertEquals(expectedOutput, DiffParser.getMapOfChangesPerResourceFromDiffOutput(diffOutput));
	}
}
