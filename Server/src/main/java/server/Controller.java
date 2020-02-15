package server;

import Spi.Request;
import Spi.Response;
import se.iths.PluginSearcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;


public class Controller {
    private Request request = null;
    private Response response = null;
    private Properties prop = new Properties();
    private File WEB_ROOT;
    private PluginSearcher pl;
    private   String DEFAULT_FILE = "/index.html";
    private   String FILE_NOT_FOUND = "/404.html";

    public Controller(Request request, Response response,Properties prop)  {
        this.request = request;
        this.response = response;
        this.prop=prop;
        WEB_ROOT = new File(prop.getProperty("WSL.StaticFilesRoot"));
        pl =new PluginSearcher(prop.getProperty("WSL.Pluginroot"));
    }

    public void processHandler() throws IOException {
        if(request.fileRequested.equals("/")){
            request.fileRequested=DEFAULT_FILE;
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
               