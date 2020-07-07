package at.cyan.codechallange.rssanalyser;

import at.cyan.codechallange.rssanalyser.dto.FeedItemDTO;
import at.cyan.codechallange.rssanalyser.dto.HotTopicDTO;
import at.cyan.codechallange.rssanalyser.model.FeedItem;
import at.cyan.codechallange.rssanalyser.model.Topic;
import at.cyan.codechallange.rssanalyser.repository.FeedItemRepository;
import at.cyan.codechallange.rssanalyser.repository.TopicRepository;
import at.cyan.codechallange.rssanalyser.service.TopicService;
import at.cyan.codechallange.rssanalyser.util.FeedHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceTests {

    private static final String FIRST_TITLE = "To Democrats, Donald Trump Is No Longer a Laughing Matter Burundi military sites attacked, 12 insurgents killed San Bernardino divers return to lake seeking electronic evidence";
    private static final String SECOND_TITLE = " Attacks on Military Camps in Burundi Kill Eight Saudi Women to Vote for First Time Platini Dealt Further Blow in FIFA Presidency Bid";

    private UUID randomId;

    @Autowired
    private FeedHandler feedHandler;

    @MockBean
    private TopicRepository topicRepository;

    @MockBean
    private FeedItemRepository feedItemRepository;

    @Autowired
    private TopicService topicService;



    @Before
    public void setUp() {
        randomId = UUID.randomUUID();

        List<Topic> topicsFirstTitle = feedHandler.extractTopicsAndSetUUID(FIRST_TITLE, randomId);
        List<Topic> topicsSecondTitle = feedHandler.extractTopicsAndSetUUID(SECOND_TITLE, randomId);

        FeedItemDTO feedItemDTO = new FeedItemDTO();
        feedItemDTO.setLink("www.google.com");
        feedItemDTO.setOriginalTitle("covid is dangerous");

        FeedItemDTO feedItemDTO2 = new FeedItemDTO();
        feedItemDTO2.setLink("www.msn.com");
        feedItemDTO2.setOriginalTitle("covid is ok");

        FeedItemDTO feedItemDTO3 = new FeedItemDTO();
        feedItemDTO3.setLink("www.bbc.com");
        feedItemDTO3.setOriginalTitle("covid is lala");

        List<FeedItemDTO> feedItemDTOS = new ArrayList<>();
        feedItemDTOS.add(feedItemDTO);
        feedItemDTOS.add(feedItemDTO2);
        feedItemDTOS.add(feedItemDTO3);

        FeedItem feedItem2 = FeedItem.builder()
                .uuid(randomId)
                .link("www.google.com/rss")
                .title(SECOND_TITLE)
                .topics(topicsSecondTitle).build();

        Mockito.when(feedItemRepository.getFeedItemDTObyKeywordAndUUID("covid" ,randomId))
                .thenReturn(feedItemDTOS);

    }

    @Test
    public void testTopicService() {
        List<HotTopicDTO> hotTopicDTOS = topicService.getHotTopicsBasedOnFrequency(randomId.toString());

    }

    @Test
    public void testFeedService() {
        List<FeedItemDTO> feedItemDTOS = feedItemRepository.getFeedItemDTObyKeywordAndUUID("covid", randomId);
        assertThat(feedItemDTOS.size())
                .isEqualTo(3);
    }


}
