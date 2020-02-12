package server;

import java.util.*;

public class Request {
    private String host;
    private String  hostAndPath;
    private String method;
    private String fileRequested = null;
    private byte[] ContentLength;

    Map<String,String> params = new HashMap<>();
    List<String> headers = new ArrayList<>();

    public Request(){
    }

    public void setMethod(String method){
        this.method=method;
    }

    public String getMethod() {
        // we parse the request with a string tokenizer
        StringTokenizer parse = new StringTokenizer(this.headers.get(0));
        method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
        setFileRequested(parse.nextToken().toLowerCase());
        return method;

    }

    public String getContentType(){
        var c  = headers.stream().filter(h -> h.startsWith("Content-Type: ")).findFirst();
        return c.map(s -> s.substring(s.indexOf(" ") + 1)).orElse("");
    }
    public int getContentLength(){
        return 0;
    }
    public boolean isKeepAlive(){
        return true;
    }

    public String getFileRequested() {
        return fileRequested;
    }

    public void setFileRequested(String fileRequested) {
        this.fileRequested = fileRequested;
    }
}
