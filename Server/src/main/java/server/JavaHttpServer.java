package server;

import Spi.Request;
import Spi.Response;
import se.iths.PluginSearcher;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// The tutorial can be found just here on the SSaurel's Blog :
// https://www.ssaurel.com/blog/create-a-simple-http-web-server-in-java
// Each Client Connection will be managed in a dedicated Thread
public class JavaHttpServer implements Runnable{
    List<String> listan =new ArrayList<>();
    private String [] args;
    static final File WEB_ROOT = new File(".");
    static ExecutorService threadManager = Executors.newCachedThreadPool();
    static final String DEFAULT_FILE = "index.html";
    static final String FORM_FILE = "form.html";
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "not_supported.html";
    // port to listen connection
    static final int PORT = 80;

    // verbose mode
    static final boolean verbose = true;

    // Client Connection via Socket Class
    private Socket connect;

    public JavaHttpServer(Socket c,String[] args) {
        connect = c;
       this.args = args;
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

            // we listen until user halts server execution
            while (true) {
                JavaHttpServer myServer = new JavaHttpServer(serverConnect.accept(),args);

                if (verbose) {
                    System.out.println("Connecton opened. (" + new Date() + ")");
                }

                // create dedicated thread to manage the client connection
                threadManager.submit(myServer);
                // Thread thread = new Thread(myServer);
                // thread.start();
            }

        } catch (IOException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        PluginSearcher pl = new PluginSearcher(args);
        // we manage our particular client connection
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;
        String fileRequested = null;


        //try (Socket socket = connect)
        try{
            Request request= new Request();
            Response response= new Response();
            // we read characters from the client via input stream on the socket
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            // we get character output stream to client (for headers)
            out = new PrintWriter(connect.getOutputStream());
            // get binary output stream to client (for requested data)
            dataOut = new BufferedOutputStream(connect.getOutputStream());
            try {
                connect.setSoTimeout(15000);
            }catch (SocketException e){
                e.printStackTrace();
            }

            while (in.ready()){
                request.headers.add(in.readLine());
            }
            if(connect.isClosed()){
                in.close();
                out.close();
            }
            request.parseHeader();
            pl.setGET(request.getMethod());
            String method=request.getMethod();


            // we get file requested

            fileRequested = request.getFileRequested();
            if(!(fileRequested==null)){

                pl.setFilerquest(fileRequested);
            }
            // we support only GET and HEAD methods, we check
            if (!method.equals("GET")  &&  !method.equals("HEAD")) {
                if (verbose) {
                    System.out.println("501 Not Implemented : " + method + " method.");
                }

                // we return the not supported file to the client
                File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
                int fileLength = (int) file.length();
                String contentMimeType = "text/html";
                //read content to return to client
                byte[] fileData = readFileData(file, fileLength);

                // we send HTTP Headers with data to client
                out.print("HTTP/1.1 501 Not Implemented\r\n");
                out.print("Server: Java HTTP Server from Golare har inga Polare\r\n");
                out.print("Date: " + new Date() + "\r\n");
                out.print("Content-type: " + contentMimeType + "\r\n");
                out.print("Content-length: " + fileLength + "\r\n");
                out.print("\r\n"); // blank line between headers and content, very important !
                out.flush(); // flush character output stream buffer
                // file
                dataOut.write(fileData, 0, fileLength);
                dataOut.flush();

            } else {
                pl.run(response , request);

                // GET or HEAD method
                if (fileRequested.endsWith("/")) {

                   // fileRequested += DEFAULT_FILE;
                    fileRequested += FORM_FILE;
                }

                File file = new File(WEB_ROOT, fileRequested);
                int fileLength = (int) file.length();
                String content = getContentType(fileRequested);

                if (method.equals("GET")) { // GET method so we return content
                    byte[] fileData = readFileData(file, fileLength);

                    // send HTTP Headers
                    out.print("HTTP/1.1 200 OK\r\n");
                    out.print("Server: Java HTTP Server from Golare har inga Polare\r\n");
                    out.print("Date: " + new Date() + "\r\n");
                    out.print("Content-type: " + content + "\r\n");
                    if(response.getContentLenght() > 0) {
                        out.print("Content-length: " + response.getContentLenght() + "\r\n");
                        out.print("\r\n"); // blank line between headers and content, very important !
                        out.flush(); // flush character output stream buffer

                        dataOut.write(response.getBody(), 0, (int) response.getContentLenght());
                        dataOut.flush();
                    }
                    else {
                        out.print("Content-length: " + fileLength + "\r\n");
                        out.print("\r\n"); // blank line between headers and content, very important !
                        out.flush(); // flush character output stream buffer

                        dataOut.write(fileData, 0, fileLength);
                        dataOut.flush();
                    }
                }

                if (verbose) {
                    System.out.println("File " + fileRequested + " of type " + content + " returned");
                }

            }

        } catch (FileNotFoundException fnfe) {
            try {
                fileNotFound(out, dataOut, fileRequested);
            } catch (IOException ioe) {
                System.err.println("Error with file not found exception : " + ioe.getMessage());
            }

        } catch (IOException ioe) {
            System.err.println("Server error : " + ioe);
        } finally {
            try {
               // in.close();
                out.close();
                dataOut.close();
                connect.close(); // we close socket connection
            } catch (Exception e) {
                System.err.println("Error closing stream : " + e.getMessage());
            }

            if (verbose) {
                System.out.println("Connection closed.\n");
            }
        }


    }

    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null)
                fileIn.close();
        }

        return fileData;
    }

    // return supported MIME Types

    private String getContentType(String fileRequested) {
            if (fileRequested.endsWith(".htm")  ||  fileRequested.endsWith(".html"))
                return "text/html";
            if (fileRequested.endsWith(".pdf"))
                return "application/pdf";
            if(fileRequested.endsWith(".css"))
                return "text/css";
            else
                return "text/plain";
        }



    private void fileNotFound(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
        File file = new File(WEB_ROOT, FILE_NOT_FOUND);
        int fileLength = (int) file.length();
        String content = "text/html";
        byte[] fileData = readFileData(file, fileLength);

        out.print("HTTP/1.1 404 File Not Found\r\n");
        out.print("Server: Java HTTP Server from Golare har inga Polare\r\n");
        out.print("Date: " + new Date() + "\r\n");
        out.print("Content-type: " + content + "\r\n");
        out.print("Content-length: " + fileLength + "\r\n");
        out.print("\r\n"); // blank line between headers and content, very important !
        out.flush(); // flush character output stream buffer

        dataOut.write(fileData, 0, fileLength);
        dataOut.flush();

        if (verbose) {
            System.out.println("File " + fileRequested + " not found");
        }
    }

}
