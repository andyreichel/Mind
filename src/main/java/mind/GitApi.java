package mind;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

public interface GitApi {
	void cloneBranch(String branch) throws IOException, InvalidRemoteException, TransportException, GitAPIException;
	void setRepository(String branch) throws IOException;
	
}
