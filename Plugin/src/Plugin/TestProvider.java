package Plugin;

import spi.Page;
import spi.PageProvider;

public class TestProvider implements PageProvider {
    @Override
    public Page create() {
        return new PluginTest();
    }
}
