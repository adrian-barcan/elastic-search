package ro.abarcan.elasticsearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticConfig {

    RestClient restClient = RestClient
            .builder(HttpHost.create("http://localhost:9200"))
            .build();

    // Create the transport with a Jackson mapper
    ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper());

    // And create the API client
    ElasticsearchClient esClient = new ElasticsearchClient(transport);

}