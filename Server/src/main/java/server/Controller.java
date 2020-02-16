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
    private DBparser dBparser;
    private long counter;
    public Controller(Request request, Response response,Properties prop)  {
        this.request = request;
        this.response = response;
        this.counter=0;
        WEB_ROOT = new File(prop.getProperty("WSL.StaticFilesRoot"));
        pl =new PluginSearcher(prop.getProperty("WSL.Pluginroot"));
        FILE_NOT_FOUND=prop.getProperty("WSL.FILE_NOT_FOUND");
        DEFAULT_FILE=prop.getProperty("WSL.DEFAULT_FILE");
    }

    public void processHandler() throws IOException {
        /*
        counter++;
        dbparser = new DBparser(x,counter);
         */
        if(request.fileRequested.equals("/")){
            request.fileRequested="/"+DEFAULT_FILE;
        }

        if(Files.exists(Paths.get(WEB_ROOT + request.fileRequested))) {
            response.setResponseCode("200 ok");
            String s = request.fileRequested.toLowerCase();
            int start=s.indexOf(".");
            switch(s.substring(start,s.length())) {
                case ".jpg":
                    response.setContentType("image/jpeg");
                    break;
                case ".jpeg":
                    response.setContentType("image/jpeg");
                    break;
                case ".jfif":
                    response.setContentType("image/jpeg");
                    break;
                case ".pjpeg":
                    response.setContentType("image/jpeg");
                    break;
                case "pjp":
                    response.setContentType("image/jpeg");
                    break;
                case ".png":
                    response.setContentType("image/png");
                    break;
                case ".apng":
                    response.setContentType("image/apng");
                    break;
                case ".gif":
                    response.setContentType("image/gif");
                    break;
                case ".bmp":
                    response.setContentType("image/bmp");
                    break;
                case ".svg":
                    response.setContentType("image/svg+xml");
                    break;
                case ".tif":
                    response.setContentType("image/tiff");
                    break;
                case ".tiff":
                    response.setContentType("image/tiff");
                    break;
                case ".js":
                    response.setContentType("text/x-js");
                    break;
                case ".css":
                    response.setContentType("text/css");
                    break;
                case ".json":
                    response.setContentType("application/json");
                    break;
                case ".pdf":
                    response.setContentType("application/pdf");
                    break;
                case ".htm":
                case ".html":
                    response.setContentType("text/html");
                    break;

                default:
                    response.setContentType("text/plain");
            }
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
               