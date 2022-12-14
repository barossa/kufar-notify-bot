package by.kufar.bot.service.impl;

import by.kufar.bot.entity.Advertisement;
import by.kufar.bot.entity.KufarSearchResponse;
import by.kufar.bot.entity.SearchRequest;
import by.kufar.bot.entity.User;
import by.kufar.bot.handler.impl.CountResponse;
import by.kufar.bot.repo.AdvertisementsRepository;
import by.kufar.bot.repo.SearchRequestRepository;
import by.kufar.bot.service.AdvertisementService;
import by.kufar.bot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

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
    private final AdvertisementsRepository advertisementsRepository;
    private final UserService userService;

    @Override
    public Set<Advertisement> findAll(String query) {
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
        return advertisements;
    }

    @Override
    public SearchRequest registerSearch(String query, User user) {
        Optional<SearchRequest> requestOptional = searchRequestRepository.findSearchRequestByQueryEqualsIgnoreCase(query);
        SearchRequest request;
        if (requestOptional.isPresent()) {
            request = requestOptional.get();
            request.getUsers().add(user);
        } else {
            List<Advertisement> advertisements = advertisementsRepository.saveAll(findAll(query));
            request = searchRequestRepository.save(new SearchRequest(query, advertisements, Collections.singleton(user)));
            log.debug("Registered new search query: \"{}\"", query);
        }
        return request;
    }

    @Override
    public Optional<SearchRequest> findByQuery(String query) {
        return searchRequestRepository.findSearchRequestByQueryEqualsIgnoreCase(query);
    }

    @Override
    public List<SearchRequest> findUserSearches(User user) {
        return searchRequestRepository.findSearchRequestsByUsersContaining(user);
    }

    @Override
    public void updateSearchResults() {
        log.info("Searching for advertisement updates...");
        List<SearchRequest> requests = searchRequestRepository.findAll();
        Map<User, Set<Advertisement>> notifications = new HashMap<>();
        for (SearchRequest request : requests) {
            Set<Advertisement> updates = updateSearchResults(request);

            //aggregate user updates for all searches
            for (User user : request.getUsers()) {
                Set<Advertisement> userUpdates = notifications.get(user);
                if (userUpdates == null) {
                    userUpdates = new HashSet<>();
                }
                userUpdates.addAll(updates);
                notifications.put(user, userUpdates);
            }
        }
        notifications.forEach(((user, advertisements) -> userService.notify(user, new ArrayList<>(advertisements))));
    }

    @Override
    public Set<Advertisement> updateSearchResults(SearchRequest request) {
        Set<Advertisement> advertisements = request.getAdvertisements();
        Set<Advertisement> allUpdates = new HashSet<>();

        Set<Advertisement> updates;
        Optional<String> optionalToken = Optional.empty();
        do {
            String nextToken = optionalToken.orElse("");
            KufarSearchResponse response = doSearch(request.getQuery(), nextToken, SORT_BY_DATE_DESC);
            updates = response.getAds().stream()
                    .filter(a -> !advertisements.contains(a))
                    .map(advertisementsRepository::save)
                    .collect(Collectors.toSet());
            optionalToken = findNextToken(response);
            advertisements.addAll(updates);
            allUpdates.addAll(updates);

        } while (updates.size() == MAX_PAGE_SIZE && optionalToken.isPresent());

        return allUpdates;
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