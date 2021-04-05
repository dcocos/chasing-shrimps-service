package com.dcocos.chasingshrimps.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;

@Configuration
public class KsqlDbConfig {

    @Value("${ksqldb.host}")
    private String ksqlDbHost;
    @Value("${ksqldb.port}")
    private Integer ksqlDbPort;

    @Bean
    public Client ksqlDbClient() {
        ClientOptions options = ClientOptions.create()
                .setHost(ksqlDbHost)
                .setPort(ksqlDbPort);
        return Client.create(options);
    }

}
