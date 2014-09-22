package mind;

import java.io.IOException;
import java.util.HashMap;

public interface BranchComparer {
	HashMap<String, Integer> getMapWithNumberOfChangesPerResource(String branchName1, String branchName2) throws IOException;
}
