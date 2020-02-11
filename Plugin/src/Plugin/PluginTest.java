package Plugin;

import Spi.Page;


public class PluginTest implements Page {
   @Override
    public void execute() {
        System.out.println("Plugin Test prints ! ");

    }

}
