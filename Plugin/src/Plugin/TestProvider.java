package Plugin;

import Spi.Page;
import Spi.PageProvider;

public class TestProvider implements PageProvider {
    @Override
    public Page create() {
        return new PluginTest();
    }
}
