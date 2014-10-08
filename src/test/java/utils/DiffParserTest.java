package utils;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import utils.DiffParser;


public class DiffParserTest {
	@Test
	public void test_getMapOfChangesPerResourceFromDiffOutput_oneEntry() throws IOException
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
	public void test_getMapOfChangesPerResourceFromDiffOutput_twoEntry() throws IOException
	{
		String diffOutput = 
				"diff --git a/README b/README" + "\n" +
				"index 5968938..8b5aae8 100644" + "\n" +
				"--- a/README" + "\n" +
				"+++ b/README" + "\n" +
				"@@ -2 +2 @@" + "\n" +
				"-the first row" + "\n" +
				"+" + "\n" +
				"@@ -4 +4 @@" + "\n" +
				"-the third row" + "\n" +
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
		expectedOutput.put("src/SOMEOTHERFILE", 3);
		Assert.assertEquals(expectedOutput, DiffParser.getMapOfChangesPerResourceFromDiffOutput(diffOutput));
	}
	
	
	@Test
	public void test_getMapOfChangesPerResourceFromDiffOutput_renamed() throws IOException
	{
		String diffOutput = "--- /dev/null" + "\n" +
		"+++ b/src/RENAMED" + "\n" +
		"@@ -0,0 +1,8 @@" + "\n" +
		"+asdfa" + "\n" +
		"+asdfasdfasd" + "\n" +
		"+Hier ist die änderung" + "\n" +
		"+asdfaasdf" + "\n" +
		"+" + "\n" +
		"+asdfaad" + "\n" +
		"+asdf" + "\n" +
		"+neu" + "\n" +
		"diff --git a/src/SOMEOTHERFILE b/src/SOMEOTHERFILE" + "\n" +
		"deleted file mode 100644" + "\n" +
		"index dd949e0..0000000" + "\n" +
		"--- a/src/SOMEOTHERFILE" + "\n" +
		"+++ /dev/null" + "\n" +
		"@@ -1,7 +0,0 @@" + "\n" +
		"-asdfa" + "\n" +
		"-asdfasdfasd" + "\n" +
		"-f" + "\n" +
		"-asdfaasdf" + "\n" +
		"-af" + "\n" +
		"-asdfaad" + "\n" +
		"-asdf" + "\n";
		
		HashMap<String, Integer> expectedOutput = new HashMap<String, Integer>();
		Assert.assertEquals(expectedOutput, DiffParser.getMapOfChangesPerResourceFromDiffOutput(diffOutput));
	}
		
	@Test
	public void test_getMapOfChangesPerResourceFromDiffOutput_noOutput() throws IOException
	{
		String diffOutput = "";

		HashMap<String, Integer> expectedOutput = new HashMap<String, Integer>();
		Assert.assertEquals(expectedOutput, DiffParser.getMapOfChangesPerResourceFromDiffOutput(diffOutput));
	}
}
