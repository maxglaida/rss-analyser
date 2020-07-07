package at.cyan.codechallange.rssanalyser.repository;

import at.cyan.codechallange.rssanalyser.dto.KeywordAndFrequencyDTO;
import at.cyan.codechallange.rssanalyser.model.FeedItem;
import at.cyan.codechallange.rssanalyser.model.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TopicRepository extends JpaRepository<Topic, Long> {

   @Query("SELECT new at.cyan.codechallange.rssanalyser.dto.KeywordAndFrequencyDTO(t.keyword, COUNT(t)) " +
           "FROM Topic t " +
           "WHERE t.feedItem.uuid = :uuid " +
           "GROUP BY t.keyword " +
           "ORDER BY Count(t) DESC, t.keyword ASC")
   List<KeywordAndFrequencyDTO> getFrequencyAndKeyword(UUID uuid, Pageable pageable);


}
