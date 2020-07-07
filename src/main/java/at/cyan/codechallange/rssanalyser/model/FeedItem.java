package at.cyan.codechallange.rssanalyser.model;

import lombok.*;

import javax.persistence.*;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(FeedItem.FeedItemId.class)
public class FeedItem {

    @Id
    private UUID uuid;

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 4096)
    private String title;

    @Column(length = 4096)
    private String link;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "feed_item_id", referencedColumnName = "id")
    @JoinColumn(name = "feed_item_uuid", referencedColumnName = "uuid")
    private List<Topic> topics;

    public static class FeedItemId implements Serializable {
        private static final long serialVersionUID = 1L;

        private UUID uuid;

        private Long id;

        @Override
        public boolean equals(Object o) {

            if (o == this) {
                return true;
            }
            if (!(o instanceof FeedItem)) {
                return false;
            }
            FeedItem feedItem = (FeedItem) o;
            return Objects.equals(uuid, feedItem.getUuid()) &&
                    Objects.equals(id, feedItem.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(uuid, id);
        }
    }


}
