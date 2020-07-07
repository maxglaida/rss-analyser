package at.cyan.codechallange.rssanalyser.controller.api.v1;

import at.cyan.codechallange.rssanalyser.dto.HotTopicDTO;
import at.cyan.codechallange.rssanalyser.model.Topic;
import at.cyan.codechallange.rssanalyser.service.FeedService;
import at.cyan.codechallange.rssanalyser.service.TopicService;
import com.rometools.rome.io.FeedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
public class FeedController {

    private final FeedService feedService;

    private final TopicService topicService;

    @Autowired
    public FeedController(FeedService feedService, TopicService topicService) {
        this.feedService = feedService;
        this.topicService = topicService;
    }

    @PostMapping("/analyse/new")
    public ResponseEntity<String> addNew(@RequestBody final List<URI> urls) {
        try {
            if(urls.size() < 2 || urls.size() > 10) {
                log.error(String.format("too many or too little urls were provided = %d , %s", urls.size(), urls));
                return ResponseEntity.badRequest().body("Only more than 2 or less than 10 valid Urls are allowed");
            }
            log.info(String.format("starting to process and analyse from endpoint addNew, urls = %s", urls));
            return ResponseEntity.ok(feedService.getFeedsAndAnalyze(Flux.fromIterable(urls)).toString());

        } catch (Exception e) {
            log.error("an error has occured", e);
            //TODO error handling to various errors and specific responses for the errors..
        }

        return ResponseEntity.unprocessableEntity().body("an error has occured, could not process request");

    }

    @GetMapping("/frequency/{uuid}")
    public ResponseEntity<List<HotTopicDTO>> getTopThreeHotTopics(@PathVariable String uuid) {
        try {
            List<HotTopicDTO> result = topicService.getHotTopicsBasedOnFrequency(uuid);
            return result.isEmpty() ? new ResponseEntity<>(HttpStatus.BAD_REQUEST) : ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("an error has occured", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
