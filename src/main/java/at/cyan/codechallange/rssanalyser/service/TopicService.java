package at.cyan.codechallange.rssanalyser.service;

import at.cyan.codechallange.rssanalyser.dto.FeedItemDTO;
import at.cyan.codechallange.rssanalyser.dto.HotTopicDTO;
import at.cyan.codechallange.rssanalyser.dto.KeywordAndFrequencyDTO;
import at.cyan.codechallange.rssanalyser.repository.FeedItemRepository;
import at.cyan.codechallange.rssanalyser.repository.TopicRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TopicService {

    private final FeedItemRepository feedItemRepository;

    private final TopicRepository topicRepository;

    @Autowired
    public TopicService(FeedItemRepository feedItemRepository, TopicRepository topicRepository) {
        this.feedItemRepository = feedItemRepository;
        this.topicRepository = topicRepository;
    }

    public List<HotTopicDTO> getHotTopicsBasedOnFrequency(String uuid) {
        UUID requestID = UUID.fromString(uuid);

        List<KeywordAndFrequencyDTO> keywordAndCount = getKeywordAndFrequencyDTOs(requestID);
        List<HotTopicDTO> hotTopicDTOS = createHotTopicsFromKeyWordAndCount(keywordAndCount);
        complementWithFeedItemDAOs(hotTopicDTOS, requestID);
        return hotTopicDTOS;
    }

    private List<KeywordAndFrequencyDTO> getKeywordAndFrequencyDTOs(UUID requestID) {
        return topicRepository.getFrequencyAndKeyword(requestID, PageRequest.of(0, 3));
    }

    private List<HotTopicDTO> createHotTopicsFromKeyWordAndCount(List<KeywordAndFrequencyDTO> keywordAndCount) {
        return keywordAndCount.stream()
                .map(entry -> HotTopicDTO.builder().frequency(entry.getFrequency()).topic(entry.getKeyword()).build())
                .collect(Collectors.toList());
    }

    private void complementWithFeedItemDAOs(List<HotTopicDTO> hotTopicDTOS, UUID requestID) {
        for (HotTopicDTO hotTopic : hotTopicDTOS) {
            List<FeedItemDTO> feedItemDTOS = feedItemRepository.getFeedItemDTObyKeywordAndUUID(hotTopic.getTopic(), requestID);
            hotTopic.setOriginalFeedItems(feedItemDTOS);
        }
    }

}
