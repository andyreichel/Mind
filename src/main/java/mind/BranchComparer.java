package mind;

import java.util.HashMap;

public interface BranchComparer {
	int getNumberOfLOCtouched(String branchName1, String branchName2);
	HashMap<String, Integer> getMapWithNumberOfChangesPerResource(String branchName1, String branchName2);
}
