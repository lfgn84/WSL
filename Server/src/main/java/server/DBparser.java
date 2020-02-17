package server;

import Spi.Request;
import Spi.Response;
import com.mongodb.Block;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBparser {
    private Response response;

    public DBparser(Response response){
        this.response = response;
    }

    public void DBgetter(Request request) {

        String field;
        String value;
        if (request.headers.get(request.headers.size() - 1).contains("name")){
            field = "name";
        }else{
            field="e-mail";
        }
        value = request.headers.get(request.headers.size()-1).substring(request.headers.get(request.headers.size()-1).indexOf(":\"")+2,request.headers.get(request.headers.size()-1).lastIndexOf("}")-1);
        List<String> answerlist = new ArrayList<>();
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("WSL");
        MongoCollection<Document> coll = database.getCollection("Greetings");
        Block<Document> printBlock = new Block<Document>() {
            @Override
            public void apply(final Document document) {
                answerlist.add(document.toJson());
                System.out.println(document.toJson());
            }
        };
        coll.find(eq(field, value)).forEach(printBlock);
        String s="";
        for (int i = 0; i < answerlist.size() ; i++) {
            s=s+answerlist.get(i);
        }
        mongoClient.close();
        response.setBody(s);
    }

    public void DBputter (String toParse, long counter, Properties prop, Response response) throws IOException {

        String y = toParse.substring(toParse.lastIndexOf(toParse));
        String z = (y.replace("&"," ").replace("%40","@"));
        String [] splitBySpace = z.split(" ");
        String name = splitBySpace[0].replace("name=","");
        String email = splitBySpace[1].replace("email=","");
        String commentPlus = splitBySpace[2].replace("comments=","").replace("+"," ");
        String comment = commentPlus.replace("+"," ");

        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("WSL");
        MongoCollection<Document> coll = database.getCollection("Greetings");
        Block<Document> printBlock = new Block<Document>() {
            @Override
            public void apply(final Document document) {
                response.setBody(document.toJson());
                System.out.println(document.toJson());
            }
        };


        Document doc1 = new Document
                ("name", name)
                .append("e-mail", email)
                .append("comment",comment);


        coll.insertOne(doc1);
        System.out.println("");
        System.out.println("ALL DOCUMENTS:\n");
        coll.find().forEach(printBlock);
        System.out.println("");
        mongoClient.close();

    }

}