package at.cyan.codechallange.rssanalyser.util;

import at.cyan.codechallange.rssanalyser.model.FeedItem;
import at.cyan.codechallange.rssanalyser.model.Topic;
import at.cyan.codechallange.rssanalyser.repository.FeedItemRepository;
import com.rometools.rome.feed.synd.SyndEntry;
import io.github.crew102.rapidrake.RakeAlgorithm;
import io.github.crew102.rapidrake.model.RakeParams;
import io.github.crew102.rapidrake.model.Result;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeedHandler {

    final
    FeedItemRepository feedItemRepository;

    private static final String DELIMITERS = "[-,.?():;\"!/]";
    private static final String POS_MODEL = "text-analysis/en-pos-maxent.bin";
    private static final String SENTENCES_MODEL = "text-analysis/en-sent.bin";
    private static final String STOP_WORDS_FILE = "text-analysis/stopwords.txt";
    private static final String[] STOP_POS = {"VB", "VBD", "VBG", "VBN", "VBP", "VBZ"};
    private static final int MIN_WORD_CHAR = 5;
    private static final boolean SHOULD_STEM = true;

    @Autowired
    public FeedHandler(FeedItemRepository feedItemRepository) {
        this.feedItemRepository = feedItemRepository;
    }

    @Async
    public void analyseAndSave(UUID requestID, List<SyndEntry> syndEntryList) {

        List<FeedItem> feeds = syndEntryList.stream()
                .map((SyndEntry rawItem) -> createEntities(rawItem, requestID))
                .collect(Collectors.toList());

        for (FeedItem feed : feeds) {
            feed.setTopics(extractTopicsAndSetUUID(feed.getTitle(), requestID));
        }

        saveAll(feeds);

    }

    private void saveAll(List<FeedItem> analysedFeeds) {
        feedItemRepository.saveAll(analysedFeeds);
    }

    private FeedItem createEntities(SyndEntry rawItem, UUID requestID) {
        FeedItem feedEntry = FeedItem.builder().uuid(requestID).build();
        return setAttributes(feedEntry, rawItem);
    }

    private FeedItem setAttributes(FeedItem feedItem, SyndEntry rawItem) {
        feedItem.setLink(rawItem.getLink());
        feedItem.setTitle(rawItem.getTitle());
        return feedItem;
    }

    public List<Topic> extractTopicsAndSetUUID(String rawItemTitle, UUID requestID) {
        Result analysisResult = null;

        try {
            analysisResult = fireRake(rawItemTitle);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert analysisResult != null;

        return createTopics(analysisResult, requestID);
    }

    private List<Topic> createTopics(Result analysisResult, UUID requestID) {
        List<Topic> topics = new ArrayList<>();
        for (String topic : analysisResult.getFullKeywords()) {
            topics.add(Topic.builder().keyword(topic).build());
        }
        return topics;
    }

    private Result fireRake(String title) throws IOException {
        RakeAlgorithm rakeAlg = rakeSetup();
        return rakeAlg.rake(title);
    }

    private RakeAlgorithm rakeSetup() throws IOException {
        String[] stopWords = loadStopWordsFile();
        RakeParams params = new RakeParams(stopWords, STOP_POS, MIN_WORD_CHAR, SHOULD_STEM, DELIMITERS);
        return new RakeAlgorithm(params, POS_MODEL, SENTENCES_MODEL);
    }

    private String[] loadStopWordsFile() throws IOException {
        InputStream stream = new FileInputStream(STOP_WORDS_FILE);
        return IOUtils.readLines(stream, "UTF-8").toArray(new String[0]);
    }

}
