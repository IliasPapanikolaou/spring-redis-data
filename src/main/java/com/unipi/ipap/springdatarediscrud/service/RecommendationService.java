package com.unipi.ipap.springdatarediscrud.service;

import com.redislabs.redisgraph.ResultSet;
import com.redislabs.redisgraph.graph_entities.Node;
import com.redislabs.redisgraph.impl.api.RedisGraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.redislabs.redisgraph.Record;

import java.util.HashSet;
import java.util.Set;

@Service
public class RecommendationService {

    @Value("${app.graphId}")
    public String graphId;

    RedisGraph graph = new RedisGraph();

    /**
     * MATCH (u:User { id: '8675309' })-[:PURCHASED]->(ob:Book)
     * MATCH (ob)<-[:PURCHASED]-(:User)-[:PURCHASED]->(b:Book)
     * WHERE NOT (u)-[:PURCHASED]->(b)
     * RETURN distinct b, count(b) as frequency
     * ORDER BY frequency DESC
     */

    public Set<String> getBookRecommendationsFromCommonPurchasesForUser(String userId) {
        Set<String> recommendations = new HashSet<String>();

        String query = "MATCH (u:User { id: '%s' })-[:PURCHASED]->(ob:Book) " //
                + "MATCH (ob)<-[:PURCHASED]-(:User)-[:PURCHASED]->(b:Book) " //
                + "WHERE NOT (u)-[:PURCHASED]->(b) " //
                + "RETURN distinct b, count(b) as frequency " //
                + "ORDER BY frequency DESC";

        ResultSet resultSet = graph.query(graphId, String.format(query, userId));

        while (resultSet.hasNext()) {
            Record record = resultSet.next();
            Node book = record.getValue("b");
            recommendations.add(book.getProperty("id").getValue().toString());
        }

        return recommendations;
    }

    /**
     * MATCH (u:User)-[:PURCHASED]->(b1:Book {id: '%s'})
     * MATCH (b1)<-[:PURCHASED]-(u)-[:PURCHASED]->(b2:Book)
     * MATCH rated = (User)-[:RATED]-(b2) " //
     * WITH b1, b2, count(b2) as freq, head(relationships(rated)) as r
     * WHERE b1 <> b2
     * RETURN b2, freq, avg(r.rating)
     * ORDER BY freq, avg(r.rating) DESC
     */
    public Set<String> getFrequentlyBoughtTogether(String isbn) {
        Set<String> recommendations = new HashSet<String>();

        String query = "MATCH (u:User)-[:PURCHASED]->(b1:Book {id: '%s'}) " //
                + "MATCH (b1)<-[:PURCHASED]-(u)-[:PURCHASED]->(b2:Book) " //
                + "MATCH rated = (User)-[:RATED]-(b2) " //
                + "WITH b1, b2, count(b2) as freq, head(relationships(rated)) as r " //
                + "WHERE b1 <> b2 " //
                + "RETURN b2, freq, avg(r.rating) " //
                + "ORDER BY freq, avg(r.rating) DESC";

        ResultSet resultSet = graph.query(graphId, String.format(query, isbn));
        while (resultSet.hasNext()) {
            Record record = resultSet.next();
            Node book = record.getValue("b2");
            recommendations.add(book.getProperty("id").getValue().toString());
        }
        return recommendations;
    }
}
