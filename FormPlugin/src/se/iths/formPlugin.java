package se.iths;

import Spi.POST;
import Spi.Page;
import Spi.Request;
import Spi.Response;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;


public class formPlugin implements Page  {
//    int fileLength = (int) file.length();
//    String content = getContentType(fileRequested);
    @POST
    public void execute(Request request, Response response){

        System.out.println("Post prints !");

    }

}
