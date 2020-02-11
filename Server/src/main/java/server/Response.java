package server;

public class Response {
    String responseCode = "";
    String contentType = "";
    private String content = "";
    boolean keepAlive = true;
    byte[] body;
    long contentLenght;

public void setBody(byte[] data){
    this.body = data;
    contentLenght = data.length;
    }
public void setBody(String data){ this.setBody(data.getBytes());}
}
