package at.cyan.codechallange.rssanalyser.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient configureWebClient() {

        ExchangeStrategies exStr = ExchangeStrategies.builder().codecs((configurer) -> {
            configurer.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());}).build();

        ClientHttpConnector cc = new ReactorClientHttpConnector(HttpClient.create().followRedirect(true));

        return WebClient
                .builder()
                .clientConnector(cc)
                .exchangeStrategies(exStr)
                .build();
    }
}
