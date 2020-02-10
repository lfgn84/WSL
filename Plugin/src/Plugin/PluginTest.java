package Plugin;

import spi.Page;


public class PluginTest implements Page {
   @Override
    public void execute() {
        System.out.println("Plugin Test prints ! ");

    }
}
