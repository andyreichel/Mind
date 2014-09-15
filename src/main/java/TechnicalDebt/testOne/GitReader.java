package TechnicalDebt.testOne;

import java.io.File;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

public class GitReader {
	FileRepositoryBuilder builder = new FileRepositoryBuilder();


	public GitReader() throws Exception {
		 String name = "areichel";
		 String password = "Andreas@FCMD";
		 String url = "https://git.fc-md.umd.edu/mind/mind.git";
		 
		 
		 // credentials
		 CredentialsProvider cp = new UsernamePasswordCredentialsProvider(name, password);
		 
		 // clone
		 File localPath = File.createTempFile("TestGitRepository", "");
		 localPath.delete();
		 
		 CloneCommand cc = new CloneCommand()
		 .setCredentialsProvider(cp)
		 .setDirectory(localPath)
		 .setURI(url);
		 Git git = cc.call();
		 
		 localPath.deleteOnExit();
		 

		
	}
	
	
	abstract class CustomConfigSessionFactory extends JschConfigSessionFactory
	{
	    @Override
	    protected JSch getJSch(final OpenSshConfig.Host hc, FS fs) throws JSchException {
	        JSch jsch = super.getJSch(hc, fs);
	        jsch.removeAllIdentity();
	        jsch.addIdentity( "C:\\Users\\TechDebt\\.ssh\\id_rsa" );
	        return jsch;
	    }

	}
	
}


