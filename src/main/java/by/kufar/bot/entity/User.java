package by.kufar.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Locale;
import java.util.Set;

import static by.kufar.bot.controller.util.ChatIdResolver.UNDEFINED_ID;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private long chatId;
    private UserStatus status;
    private Locale locale;
    @OneToMany(orphanRemoval = true, cascade = {PERSIST, MERGE})
    private Set<PinnedData> data;

    public void pinData(PinnedData data) {
        this.data.add(data);
    }

    public void clearData() {
        data.clear();
    }

    @Data
    @Entity
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PinnedData {
        @Id
        @GeneratedValue
        @EqualsAndHashCode.Exclude
        private long id;
        private String key;
        @EqualsAndHashCode.Exclude
        private String value;

        public PinnedData(String key, String value) {
            this.id = 0;
            this.key = key;
            this.value = value;
        }

        public static PinnedData of(String key) {
            return new PinnedData(UNDEFINED_ID, key, "");
        }
    }
}
