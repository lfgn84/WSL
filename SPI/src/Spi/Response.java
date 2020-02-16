package Spi;

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

    public byte[] getBody() {
        return body;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public long getContentLenght() {
        return contentLenght;
    }
}
