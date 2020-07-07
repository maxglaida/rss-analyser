package at.cyan.codechallange.rssanalyser.dto;

import at.cyan.codechallange.rssanalyser.model.FeedItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class HotTopicDTO {
    private String topic;
    private Long frequency;
    List<FeedItemDTO> originalFeedItems;
}
