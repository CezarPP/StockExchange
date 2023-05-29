package org.exchange.logger;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.common.fix.FixMessage;

/**
 * No-SQL database for storing order executions
 */
public class Logger {
    MongoClient client;
    MongoDatabase database;
    MongoCollection<Document> collection;

    public Logger() {
        client = MongoClients.create(); // Connect to localhost:27017 by default
        database = client.getDatabase("Exchange");
        collection = database.getCollection("Broadcasts");
        new LoggerListener(this).start();
    }

    public void insertMessage(FixMessage fixMessage) {
        Document document = new Document()
                .append("header", fixMessage.header().toString())
                .append("body", fixMessage.body().toString())
                .append("trailer", fixMessage.trailer().toString());

        collection.insertOne(document);
    }

}