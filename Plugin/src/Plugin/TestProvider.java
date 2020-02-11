package Plugin;

import Spi.Adress;
import Spi.PageProvider;

@Adress("/index.html")
public class TestProvider implements PageProvider {

    public void executes() {
        System.out.println("Adress print");

    }

}
