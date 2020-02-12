package Plugin;

import Spi.Adress;
import Spi.GET;
import Spi.Page;


@Adress("/index.html")
public class TestProvider implements Page {

    public void executes() {
        System.out.println("Adress print");

    }
    @GET
    public void hendelWasAPainter(){
        System.out.println("GET Method prints!!");
    }
}
