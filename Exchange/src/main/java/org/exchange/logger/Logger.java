package org.exchange.logger;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.common.fix.FixMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * No-SQL database for storing order executions
 */
public class Logger extends Thread {
    MongoClient client;
    MongoDatabase database;
    MongoCollection<Document> collection;

    LoggerListener loggerListener;

    public Logger() {
        client = MongoClients.create(); // Connect to localhost:27017 by default
        database = client.getDatabase("Exchange");
        collection = database.getCollection("Broadcasts");
        loggerListener = new LoggerListener(this);
    }

    @Override
    public void run() {
        loggerListener.start();
    }

    public void insertMessage(FixMessage fixMessage) {
        Document document = new Document()
                .append("header", fixMessage.header().toString())
                .append("body", fixMessage.body().toString())
                .append("trailer", fixMessage.trailer().toString());

        collection.insertOne(document);
    }

    public List<Document> getAllMessages() {
        return collection.find().into(new ArrayList<>());
    }

    public void printAllMessages() {
        System.out.println("All messages in the database are: ");
        List<Document> allMessages = getAllMessages();
        for (Document doc : allMessages) {
            System.out.println(doc.toJson());
        }
    }
}