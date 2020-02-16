package server;

import Spi.Request;
import Spi.Response;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// The tutorial can be found just here on the SSaurel's Blog :
// https://www.ssaurel.com/blog/create-a-simple-http-web-server-in-java
// Each Client Connection will be managed in a dedicated Thread
public class JavaHttpServer implements Runnable{
    private static Properties prop;
    static  File WEB_ROOT = null;
    static ExecutorService threadManager = Executors.newCachedThreadPool();

    // port to listen connection
    static  int PORT ;

    // verbose mode
    static final boolean verbose = true;

    // Client Connection via Socket Class
    private Socket connect;

    public JavaHttpServer(Socket c) {
        connect = c;
    }

    public static void main(String[] args) {
        prop=new Properties();
        try (FileInputStream setingFile = new FileInputStream("./Settings.properties")) {
            prop.load(setingFile);

        }catch(IOException e){
            e.printStackTrace();
        }

        WEB_ROOT=new File((String) prop.get("WSL.StaticFilesRoot"));
        PORT = Integer.parseInt( prop.get("WSL.port").toString());
        try {
            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

            // we listen until user halts server execution
            while (true) {
                JavaHttpServer myServer = new JavaHttpServer(serverConnect.accept());

                if (verbose) {
                    System.out.println("Connecton opened. (" + new Date() + ")");
                }

                // create dedicated thread to manage the client connection
                threadManager.submit(myServer);
            }

        } catch (IOException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }

    @Override
    public void run() {
         // we manage our particular client connection
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;

        try {
            connect.setSoTimeout(10000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (!connect.isClosed()) {
            //try (Socket socket = connect)
            try {
                Request request = new Request();
                Response response = new Response();

                Controller controller = new Controller(request,response,prop);
                // we read characters from the client via input stream on the socket
                in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                // we get character output stream to client (for headers)
                out = new PrintWriter(connect.getOutputStream());
                // get binary output stream to client (for requested data)
                dataOut = new BufferedOutputStream(connect.getOutputStream());

                readInStream(in, request);

                 request.parseHeader();

               controller.processHandler();
                sendToClient(out,dataOut,response,request);
            } catch (Exception e) {
                close(in,out,dataOut);

              //  e.printStackTrace();

            }
        }
        close(in,out,dataOut);
    }
    private void readInStream(BufferedReader in,Request request) throws IOException {
        String s;
        while (in.ready()) {
            s = in.readLine();
            request.headers.add(s);
            if (s.equals("")) {
                request.parseHeader();
                if (request.getContentType().equals("application/x-www-form-urlencoded")) {
                    char[] st = new char[request.getContentLength()];
                    for (int i = 0; i < request.getContentLength(); i++) {
                        st[i] = (char) in.read();
                    }
                    request.headers.add(String.copyValueOf(st));
                    break;
                }
            }
        }

    }

    private void close(BufferedReader in,PrintWriter out,BufferedOutputStream dataOut){
        try {
            in.close();
            out.close();
            dataOut.close();
            connect.close(); // we close socket connection
        } catch (Exception e) {
            System.err.println("Error closing stream : " + e.getMessage());
        }
    }
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

        private void sendToClient(PrintWriter out, OutputStream dataOut,Response response,Request request) throws IOException {

                // send HTTP Headers
                out.print("HTTP/1.1 "+response.getResponseCode()+"\r\n");
                out.print("Server: Java HTTP Server from Golare har inga Polare\r\n");
                out.print("Date: " + new Date() + "\r\n");
                out.print("Content-type: " + response.getContentType() + "\r\n");
                if (response.getContentLenght() > 0) {
                    out.print("Content-length: " + response.getContentLenght() + "\r\n");
                    out.print("\r\n"); // blank line between headers and content, very important !
                    out.flush(); // flush character output stream buffer

                    dataOut.write(response.getBody(), 0, (int) response.getContentLenght());
                    dataOut.flush();
                }else {
                    out.print("Content-length: " + response.getContentLenght() + "\r\n");
                    out.print("\r\n"); // blank line between headers and content, very important !
                    out.flush(); // flush character output stream buffer
                }
                if (verbose) {
                    System.out.println("File " + request.fileRequested + " of type " + response.getContentType() + " returned");
                }


        }

}
