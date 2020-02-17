package server;

import Spi.Response;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBparser {

    public DBparser (String toParse, Properties prop, Response response) {

        List<Document> documents = new ArrayList<Document>(); // Creating a Document ArrayList named "documents" where we will save our documents.
        String y = toParse.substring(toParse.lastIndexOf(toParse));
        String z = (y.replace("&"," ").replace("%40","@"));
        String [] splitBySpace = z.split(" ");
        String name = splitBySpace[0].replace("name=","");
        String email = splitBySpace[1].replace("email=","");
        String commentPlus = splitBySpace[2].replace("comments=","").replace("+"," ");
        String comment = commentPlus.replace("+"," ");

        MongoClient mongoClient = MongoClients.create("mongodb://Localhost:27017");    // Creating a mongoClient to connect with Mongodb
        MongoDatabase database = mongoClient.getDatabase("WSL"); // Creating our new database through our mongoClient (database : "lab3")
        MongoCollection<Document> coll = database.getCollection("Greetings"); // Creating our new collection in our new database (collection : "restaurants")
        Block<Document> printBlock = new Block<Document>() { // Creating a "printBlock" method that will identify and print out our documents on blocks in Json format.
            @Override
            public void apply(final Document document) {
                response.setBody(document.toJson());
                System.out.println(document.toJson());
            }
        };


        Document doc1 = new Document   //Creating our new documents and appending their fields and values for our collection("restaurants").
                 ("name", name)                   //With other words, creating our database of "restaurants" and their details on our collection.
                .append("e-mail", email)
                .append("comment",comment);

        // Adding our created documents to our "documents" ArrayList.


        try {
            coll.find();
            coll.insertOne(doc1); // Inserting our documents on our ArrayList to our created collection of documents ("restaurants") : 'coll', using the "insertMany()" mongodb command.
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("");
        System.out.println("ALL DOCUMENTS:\n");
        coll.find().forEach(printBlock); // Printing all block of documents in  our collection using "find()"  mongodb command.
        System.out.println("");

    mongoClient.close();


    }
}
