package mind;

import java.io.IOException;

public class ResourceNotFoundException extends IOException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String resourceKey;
	public ResourceNotFoundException(String resourceKey)
	{
		this.resourceKey = resourceKey;
	}
	
    public String getMessage() {
        return resourceKey + " not found!";
    }
}
