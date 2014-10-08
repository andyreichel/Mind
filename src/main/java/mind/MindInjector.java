package mind;

import interfaces.BranchComparer;
import interfaces.GitApi;
import interfaces.IssueTrackerReader;
import interfaces.MindConfiguration;
import interfaces.RCallerApi;
import interfaces.RedmineApi;
import interfaces.SCMReader;
import interfaces.SonarReader;
import interfaces.SonarRunnerApi;
import interfaces.SonarWebApi;
import interfaces.StatisticGenerator;

import com.google.inject.AbstractModule;

public class MindInjector extends AbstractModule {

	@Override
	protected void configure() {
		bind(BranchComparer.class).to(BranchComparerImpl.class);
		bind(SonarRunnerApi.class).to(SonarRunnerApiImpl.class);
		bind(GitApi.class).to(GitApiImpl.class);
		bind(SCMReader.class).to(GitReader.class);
		bind(MindConfiguration.class).to(MindConfigurationImpl.class);
		bind(SonarWebApi.class).to(SonarWebApiImpl.class);
		bind(RedmineApi.class).to(RedmineApiImpl.class);
		bind(IssueTrackerReader.class).to(RedmineReader.class);
		bind(RCallerApi.class).to(RCallerApiImpl.class);
		bind(SonarReader.class).to(SonarReaderImpl.class);
		bind(StatisticGenerator.class).to(StatisticGeneratorImpl.class);
	}

}
