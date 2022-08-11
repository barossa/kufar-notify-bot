package by.kufar.bot.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KufarSearchResponse {
    @JsonAlias("ads")
    private List<Advertisement> ads;
    private long total;
    private Pagination pagination;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private List<Page> pages;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Page {
        private String label;
        private String token;
    }
}
