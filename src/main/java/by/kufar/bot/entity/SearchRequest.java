package by.kufar.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest {
    @Id
    @GeneratedValue(generator = "search_seq")
    @SequenceGenerator(name = "search_seq", sequenceName = "search_seq", allocationSize = 1)
    private long id;

    private String query;

    @ManyToMany
    @JoinTable(
            name = "search_advertisements",
            joinColumns = @JoinColumn(name = "search_id"),
            inverseJoinColumns = @JoinColumn(name = "advertisement_id"))
    private Set<Advertisement> advertisements;

    private LocalDateTime lastUpdated;

    @ManyToMany
    @JoinTable(
            name = "user_searches",
            joinColumns = @JoinColumn(name = "search_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;

    public SearchRequest(String query, Collection<Advertisement> advertisements, Collection<User> users) {
        id = 0;
        this.query = query;
        this.advertisements = new HashSet<>(advertisements);
        this.users = new HashSet<>(users);
    }

    @PrePersist
    private void prePersist() {
        lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
