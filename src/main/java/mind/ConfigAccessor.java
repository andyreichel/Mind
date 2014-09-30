package mind;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;

public class ConfigAccessor {
	public static String getValue(Configuration config, String key) throws ConfigurationException
	{
		if(!config.containsKey(key))
		{
			throw new ConfigurationException("Key " + key + " is not configured.");
		}
		return config.getString(key);
	}
}
