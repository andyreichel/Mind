package mind;

import java.io.IOException;
import java.util.HashMap;

/**
 * Classes that implement this interface compare two files of different branches and get a Map as return value of this format:
 * class1=5
 * class2=10
 * class3=0
 * 
 *
 */
public interface BranchComparer {
	HashMap<String, Integer> getMapWithNumberOfChangesPerResource(String branchName1, String branchName2) throws IOException, NoSuchBranchException;
}
