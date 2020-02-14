package se.iths;

import Spi.*;


@Adress("/golare-har-inga-polare.html")
public class Rooster implements Page {

    public void executes() {
        System.out.println("Adress print");

    }
    @GET
    public void hendelWasAPainter(Request request, Response response){
        System.out.println("GET Method prints!!");
        response.setBody("<!doctype html>\r\n"
        +"<html lang=\"en\">\r\n"
        +"<head>\r\n"
        +"<meta charset=\"utf-8\">\r\n"
        +"<title>Index</title>\r\n"
        +"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n"
        +"<style>\r\n"
        +"{line-height: 1.2;margin: 0;}\r\n"
        +"html {color: #888;display: table;font-family: sans-serif;height: 100%;text-align: center;width: 100%;}\r\n"
        +"body {display: table-cell;vertical-align: middle;margin: 2em auto;}\r\n"
        +"h1 {color: #555;font-size: 2em;font-weight: 400;}\r\n"
        +"p {margin: 0 auto;width: 280px;}\r\n"
        +"@media only screen and (max-width: 280px) {body,p {width: 95%;}\r\n"
        +"h1 {font-size: 1.5em;margin: 0 0 0.3em;}}\r\n"
        +"</style></head><body><h1>THIS IS THE INDEX </h1><p>Welcome to the Index </p><img src=\"https://media.giphy.com/media/m9SNDj5zWIycPx2V76/giphy.gif\"></body></html>\r\n");
    }
}
