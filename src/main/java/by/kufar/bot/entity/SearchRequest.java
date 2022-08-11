package by.kufar.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest {
    @Id
    @GeneratedValue
    private long id;

    private String query;

    @OneToMany(cascade = {MERGE, PERSIST},
            orphanRemoval = true)
    private Set<Advertisement> advertisements;

    private LocalDateTime lastUpdated;

    @ManyToOne
    private User user;

    @PrePersist
    private void prePersist() {
        lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
