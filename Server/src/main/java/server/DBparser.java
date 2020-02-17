package server;

import Spi.Response;
import com.mongodb.Block;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBparser {

    public DBparser (String toParse, long counter, Properties prop, Response response) throws IOException {
     /*   ProcessBuilder pb = new ProcessBuilder();
        ProcessBuilder pb1 = new ProcessBuilder();
        pb.command(prop.getProperty("WSL.mongopath")+"mongod");
        pb1.command(prop.getProperty("WSL.mongopath")+"mongo");
        Process p;
        Process m;
        p = pb.start();
        m = pb1.start();
        System.out.println(p.isAlive());
        System.out.println(m.isAlive());
//            p.destroyForcibly();
//            m.destroyForcibly();
*/
        List<Document> documents = new ArrayList<Document>(); // Creating a Document ArrayList named "documents" where we will save our documents.
        String y = toParse.substring(toParse.lastIndexOf(toParse));
        String z = (y.replace("&"," ").replace("%40","@"));
        String [] splitBySpace = z.split(" ");
        String name = splitBySpace[0].replace("name=","");
        String email = splitBySpace[1].replace("email=","");
        String commentPlus = splitBySpace[2].replace("comments=","").replace("+"," ");
        String comment = commentPlus.replace("+"," ");

        MongoClient mongoClient = MongoClients.create();    // Creating a mongoClient to connect with Mongodb
        MongoDatabase database = mongoClient.getDatabase("WSL"); // Creating our new database through our mongoClient (database : "lab3")
        MongoCollection<Document> coll = database.getCollection("Greetings"); // Creating our new collection in our new database (collection : "restaurants")
        Block<Document> printBlock = new Block<Document>() { // Creating a "printBlock" method that will identify and print out our documents on blocks in Json format.
            @Override
            public void apply(final Document document) {
                response.setBody(document.toJson());
            }
        };


        Document doc1 = new Document   //Creating our new documents and appending their fields and values for our collection("restaurants").
                ("name", name)                   //With other words, creating our database of "restaurants" and their details on our collection.
                .append("e-mail", email)
                .append("comment",comment);

        // Adding our created documents to our "documents" ArrayList.



        coll.insertOne(doc1); // Inserting our documents on our ArrayList to our created collection of documents ("restaurants") : 'coll', using the "insertMany()" mongodb command.
        System.out.println("");
        System.out.println("ALL DOCUMENTS:\n");
        coll.find().forEach(printBlock); // Printing all block of documents in  our collection using "find()"  mongodb command.
        System.out.println("");
/*
        p.destroyForcibly();
        m.destroyForcibly();
    */
    }

}