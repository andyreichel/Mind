package mind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

public class DiffParser {
	public static HashMap<String, Integer> getMapOfChangesPerResourceFromDiffOutput(String output) throws IOException
	{
		BufferedReader bufReader = new BufferedReader(new StringReader(output));
		String line=null;
		int numberChanges = 0;
		
		HashMap<String, Integer> numberChangesPerResource = new HashMap<String, Integer>();
		String resourceName = null;
		while( (line=bufReader.readLine()) != null )
		{
			if(!(line.startsWith("--- /dev/null") || line.startsWith("+++ /dev/null")))
			{
				if(line.startsWith("+++"))
				{
					if(resourceName != null)
					{
						numberChangesPerResource.put(resourceName, numberChanges);
						numberChanges = 0;
					}
					
					resourceName = line.substring(line.lastIndexOf("/")+1);
					
				}
				if(line.startsWith("@@"))
				{
					numberChanges++;
				}
			}else
			{
				bufReader.readLine();
			}
		}
		if(resourceName != null)
			numberChangesPerResource.put(resourceName, numberChanges);
		return numberChangesPerResource;
	}
}
