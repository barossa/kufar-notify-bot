package by.kufar.bot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Advertisement {
    private static final BigDecimal BYN_DIVISOR = BigDecimal.valueOf(100);

    @Id
    @JsonProperty("ad_id")
    private long id;

    @JsonProperty("subject")
    @EqualsAndHashCode.Exclude
    private String name;

    @JsonProperty("ad_link")
    @EqualsAndHashCode.Exclude
    private String link;
    @JsonProperty("price_byn")
    @EqualsAndHashCode.Exclude
    private BigDecimal price;

    @JsonProperty("category")
    @EqualsAndHashCode.Exclude
    private long category;

    @JsonProperty("list_time")
    @EqualsAndHashCode.Exclude
    private LocalDateTime listTime;

    public BigDecimal getPrice() {
        return price.divide(BYN_DIVISOR, RoundingMode.HALF_EVEN);
    }

    @Override
    public String toString() {
        return id + "";
    }
}
