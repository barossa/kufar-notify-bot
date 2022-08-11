package by.kufar.bot.service.impl;

import by.kufar.bot.entity.Advertisement;
import by.kufar.bot.entity.KufarSearchResponse;
import by.kufar.bot.entity.SearchRequest;
import by.kufar.bot.entity.User;
import by.kufar.bot.handler.impl.CountResponse;
import by.kufar.bot.repo.SearchRequestRepository;
import by.kufar.bot.service.AdvertisementService;
import by.kufar.bot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {
    private static final String FIND_ADS_BY_PARAMETERS = "https://cre-api-v2.kufar.by/items-search/v1/engine/v1/search/rendered-paginated?lang=ru&ot=1&query={query}&cursor={cursor}&sort={sort}&size={size}";
    private static final String FIND_ADS_COUNT = "https://cre-api-v2.kufar.by/items-search/v1/engine/v1/search/count?ot=1&query={query}&sort=lst.a";
    private static final String SORT_BY_DATE_ASC = "lst.a";
    private static final String SORT_BY_DATE_DESC = "lst.d";
    private static final int MAX_PAGE_SIZE = 200;
    private final RestTemplate restTemplate;
    private final SearchRequestRepository searchRequestRepository;
    private final UserService userService;

    @Override
    public List<Advertisement> findAll(String query) {
        Set<Advertisement> advertisements = new HashSet<>();
        try {
            Optional<String> optionalToken = Optional.empty();
            do {
                String nextToken = optionalToken.orElse("");
                KufarSearchResponse response = doSearch(query, nextToken, SORT_BY_DATE_ASC);
                advertisements.addAll(response.getAds());
                optionalToken = findNextToken(response);
                log.info("Total advertisements: {}", advertisements.size());
            } while (optionalToken.isPresent());

        } catch (Exception e) {
            log.error("Can't find all advertisements by query \"{}\", cause: {}", query, e.getMessage(), e);
        }
        return new ArrayList<>(advertisements);
    }

    @Override
    public SearchRequest registerSearch(String query, User user) {
        List<Advertisement> advertisements = findAll(query);
        SearchRequest request = new SearchRequest(0, query,
                new HashSet<>(advertisements), LocalDateTime.now(), user);
        log.debug("Registered new search query: \"{}\"", query);
        return searchRequestRepository.save(request);
    }

    @Override
    public List<SearchRequest> findUserSearches(User user) {
        return searchRequestRepository.findSearchRequestsByUser(user);
    }

    @Override
    public void updateSearchResults() {
        log.info("Searching for advertisement updates...");
        List<SearchRequest> requests = searchRequestRepository.findAll();
        for (SearchRequest request : requests) {
            Set<Advertisement> advertisements = request.getAdvertisements();

            KufarSearchResponse response;
            List<Advertisement> updates;
            Optional<String> optionalToken = Optional.empty();
            do {
                String nextToken = optionalToken.orElse("");
                response = doSearch(request.getQuery(), nextToken, SORT_BY_DATE_DESC);
                updates = response.getAds().stream().filter(a -> !advertisements.contains(a)).toList();
                optionalToken = findNextToken(response);
                advertisements.addAll(updates);
                userService.notify(request.getUser(), updates);

            } while (updates.size() == MAX_PAGE_SIZE && optionalToken.isPresent());
            request.setLastUpdated(LocalDateTime.now());
        }
    }

    @Override
    public long findQuantity(String query) {
        long result = 0;
        try {
            CountResponse response = restTemplate.getForObject(FIND_ADS_COUNT, CountResponse.class, query);
            if (response != null) {
                result = response.getCount();
            }

        } catch (Exception e) {
            log.error("Can't find advertisement quantity by query \"{}\", cause: {}", query, e.getMessage(), e);
        }
        return result;
    }

    private Optional<String> findNextToken(KufarSearchResponse response) {
        return response.getPagination().getPages().stream()
                .filter(page -> page.getLabel().equals("next"))
                .map(KufarSearchResponse.Page::getToken)
                .findFirst();
    }

    private KufarSearchResponse doSearch(String query, String cursor, String sort) {
        return restTemplate.getForObject(FIND_ADS_BY_PARAMETERS, KufarSearchResponse.class, query, cursor, sort, MAX_PAGE_SIZE);
    }
}