package server;

import Spi.Request;
import Spi.Response;
import se.iths.PluginSearcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;


public class Controller {
    private Request request ;
    private Response response;
    private File WEB_ROOT;
    private PluginSearcher pl;
    private String DEFAULT_FILE ;
    private String FILE_NOT_FOUND ;

    public Controller(Request request, Response response,Properties prop)  {
        this.request = request;
        this.response = response;
        WEB_ROOT = new File(prop.getProperty("WSL.StaticFilesRoot"));
        pl =new PluginSearcher(prop.getProperty("WSL.Pluginroot"));
        FILE_NOT_FOUND=prop.getProperty("WSL.FILE_NOT_FOUND");
        DEFAULT_FILE=prop.getProperty("WSL.DEFAULT_FILE");
    }

    public void processHandler() throws IOException {
        if(request.fileRequested.equals("/")){
            request.fileRequested="/"+DEFAULT_FILE;
        }

        if(Files.exists(Paths.get(WEB_ROOT + request.fileRequested))){
            response.setResponseCode("200 ok");
            response.setContentType("text/html");

            fileReader(request.fileRequested);

        }
        else {
            pl.run(response,request);
        }

        if(response.getContentLenght()<=0){
            response.setResponseCode("404 Not Found");
            response.setContentType("text/html");
            fileReader(FILE_NOT_FOUND);
        }
    }

    private void fileReader(String fileRequested) throws IOException {
        File file = new File(WEB_ROOT, fileRequested);
        FileInputStream fileIn = null;
        byte[] fileData = new byte[(int) file.length()];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            if (fileIn != null)
                fileIn.close();
        }
        response.setBody(fileData);
    }

}
               