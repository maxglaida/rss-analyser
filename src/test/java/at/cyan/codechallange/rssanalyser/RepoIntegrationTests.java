package at.cyan.codechallange.rssanalyser;

import at.cyan.codechallange.rssanalyser.dto.FeedItemDTO;
import at.cyan.codechallange.rssanalyser.dto.KeywordAndFrequencyDTO;
import at.cyan.codechallange.rssanalyser.model.FeedItem;
import at.cyan.codechallange.rssanalyser.model.Topic;
import at.cyan.codechallange.rssanalyser.repository.FeedItemRepository;
import at.cyan.codechallange.rssanalyser.repository.TopicRepository;
import at.cyan.codechallange.rssanalyser.util.FeedHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RepoIntegrationTests {

    private static final String FIRST_TITLE = "To Democrats, Donald Trump Is No Longer a Laughing Matter Burundi military sites attacked, 12 insurgents killed San Bernardino divers return to lake seeking electronic evidence";
    private static final String SECOND_TITLE = " Attacks on Military Camps in Burundi Kill Eight Saudi Women to Vote for First Time Platini Dealt Further Blow in FIFA Presidency Bid";

    @InjectMocks
    private FeedHandler feedHandler;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FeedItemRepository feedItemRepository;

    @Autowired
    private TopicRepository topicRepository;

    private UUID randomId;

    @Before
    public void setUp() {
        randomId = UUID.randomUUID();

        List<Topic> topicsFirstTitle = feedHandler.extractTopicsAndSetUUID(FIRST_TITLE, randomId);
        List<Topic> topicsSecondTitle = feedHandler.extractTopicsAndSetUUID(SECOND_TITLE, randomId);

        FeedItem feedItem = FeedItem.builder()
                .uuid(randomId)
                .link("www.bbc.com/rss")
                .title(FIRST_TITLE)
                .topics(topicsFirstTitle).build();

        FeedItem feedItem2 = FeedItem.builder()
                .uuid(randomId)
                .link("www.google.com/rss")
                .title(SECOND_TITLE)
                .topics(topicsSecondTitle).build();
        entityManager.persist(feedItem);
        entityManager.persist(feedItem2);
        entityManager.flush();

    }

    @Test
    public void whenFindByKeywordandUUID_thenReturnFeedItemDTO() {
        List<FeedItemDTO> feedItemDTOS = feedItemRepository.getFeedItemDTObyKeywordAndUUID("democrats" ,randomId);
        assertThat(feedItemDTOS.size())
                .isGreaterThan(0);
    }

    @Test
    public void whenFindByUUID_thenReturnkeywordAndFrequencyDTOS() {
        List<KeywordAndFrequencyDTO> keywordAndFrequencyDTOS = topicRepository.getFrequencyAndKeyword(randomId, PageRequest.of(0, 3));
        assertThat(keywordAndFrequencyDTOS.size())
                .isEqualTo(3);
    }
}
