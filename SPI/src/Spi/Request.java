package Spi;

import java.util.*;

public class Request {
    public String host;
    public String  hostAndPath;
    public String method;
    public String fileRequested = null;
    public Byte[] ContentLength;

    public Map<String,String> params = new HashMap<>();
    public List<String> headers = new ArrayList<>();

    public Request(){
    }

    public void parseHeader(){
        // we parse the request with a string tokenizer
        StringTokenizer parse = new StringTokenizer(this.headers.get(0));
        method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
        setFileRequested(parse.nextToken().toLowerCase());
        var c  = headers.stream().filter(h -> h.startsWith("Host: ")).findFirst();
        this.host = c.map(s -> s.substring(s.indexOf(" ") + 1)).orElse("");

    }

    public String getMethod() {
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
