package at.cyan.codechallange.rssanalyser.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class FeedItemDTO {
    private String link;
    private String originalTitle;
}
