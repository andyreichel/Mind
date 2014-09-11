package TechnicalDebt.testOne;

import java.util.List;

import org.sonar.api.SonarPlugin;

import com.google.common.collect.ImmutableList;

public final class PluginClass extends SonarPlugin {
    public List getExtensions() {
        return ImmutableList.of(
                Decorator1.class);
    }
}