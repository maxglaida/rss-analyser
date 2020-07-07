package at.cyan.codechallange.rssanalyser.repository;

import at.cyan.codechallange.rssanalyser.dto.FeedItemDTO;
import at.cyan.codechallange.rssanalyser.model.FeedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FeedItemRepository extends JpaRepository<FeedItem, Long> {

    @Query("SELECT new at.cyan.codechallange.rssanalyser.dto.FeedItemDTO(t.feedItem.link, t.feedItem.title) FROM Topic t WHERE t.keyword = :keyword and t.feedItem.uuid = :uuid")
    List<FeedItemDTO> getFeedItemDTObyKeywordAndUUID(String keyword, UUID uuid);

}
