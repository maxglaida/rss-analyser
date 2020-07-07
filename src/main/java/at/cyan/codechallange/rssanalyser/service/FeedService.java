package at.cyan.codechallange.rssanalyser.service;

import at.cyan.codechallange.rssanalyser.util.FeedHandler;
import at.cyan.codechallange.rssanalyser.util.RSSFeedConsumer;
import com.rometools.rome.feed.synd.SyndFeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.UUID;

@Service
public class FeedService {

    private final RSSFeedConsumer feedConsumer;
    private final FeedHandler feedAnalyser;

    @Autowired
    public FeedService(FeedHandler feedAnalyser, RSSFeedConsumer feedConsumer) {
        this.feedConsumer = feedConsumer;
        this.feedAnalyser = feedAnalyser;
    }

    public UUID getFeedsAndAnalyze(Flux<URI> urlsToAnalyse) {

        Flux<SyndFeed> syndFeeds = fetchFeeds(urlsToAnalyse);

        UUID randomID = UUID.randomUUID();
        syndFeeds.subscribe(syndFeed -> {
            feedAnalyser.analyseAndSave(randomID, syndFeed.getEntries());
        });

        return randomID;
    }

    private Flux<SyndFeed> fetchFeeds(Flux<URI> urlsToAnalyse) {
        return urlsToAnalyse.parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(feedConsumer::fetch)
                .sequential();
    }

}
