package se.iths;

import Spi.POST;
import Spi.Page;
import Spi.Request;
import Spi.Response;

import java.io.File;
import java.util.Date;


public class formPlugin implements Page  {
    File file = new File(".form.html");

    @POST
    public void execute(Request request, Response response){

        System.out.println("Post prints !");
        response.setBody(file.toString());

    }

}


