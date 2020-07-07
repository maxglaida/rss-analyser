package at.cyan.codechallange.rssanalyser.util;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.CompletionException;

@Service
@Slf4j
public class RSSFeedConsumer {

    final WebClient webClient;

    @Autowired
    public RSSFeedConsumer(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<SyndFeed> fetch(final URI url) {
        return  webClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_XML)
                .retrieve().bodyToMono(String.class)
                .map(this::bodyToSyndFeed);
    }

    private SyndFeed bodyToSyndFeed(String body) {
        try {
            InputStream in = IOUtils.toInputStream(body, Charsets.toCharset("UTF-8"));
            return new SyndFeedInput().build(new XmlReader(in));
        } catch (FeedException | IOException e) {
            throw new CompletionException(e);
        }
    }

}
