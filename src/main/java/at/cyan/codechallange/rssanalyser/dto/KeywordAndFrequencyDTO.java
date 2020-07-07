package at.cyan.codechallange.rssanalyser.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class KeywordAndFrequencyDTO {
    private String keyword;
    private Long frequency;
}
