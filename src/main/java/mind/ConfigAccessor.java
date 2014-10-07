package mind;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;

/**
 * This class is used to parse values from a configuration with some basic validation.
 *
 */
public class ConfigAccessor {
	/**
	 * Method checks if a key is in the configuration and returns its value if given
	 * @param config
	 * @param key
	 * @return
	 * @throws ConfigurationException
	 */
	public static String getValue(Configuration config, String key) throws ConfigurationException
	{
		if(!config.containsKey(key))
		{
			throw new ConfigurationException("Key " + key + " is not configured.");
		}
		return config.getString(key);
	}
}
