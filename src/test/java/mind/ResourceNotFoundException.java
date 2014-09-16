package mind;

import java.io.IOException;

public class ResourceNotFoundException extends IOException {
	String resourceKey;
	public ResourceNotFoundException(String resourceKey)
	{
		this.resourceKey = resourceKey;
	}
	
    public String getMessage() {
        return resourceKey + " not found!";
    }
}
