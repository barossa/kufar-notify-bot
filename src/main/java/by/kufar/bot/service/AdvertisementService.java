package by.kufar.bot.service;

import by.kufar.bot.entity.Advertisement;
import by.kufar.bot.entity.SearchRequest;
import by.kufar.bot.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AdvertisementService {

    long findQuantity(String query);

    Set<Advertisement> findAll(String query);

    SearchRequest registerSearch(String query, User user);

    Optional<SearchRequest> findByQuery(String query);

    List<SearchRequest> findUserSearches(User user);

    void updateSearchResults();

    Set<Advertisement> updateSearchResults(SearchRequest searchRequest);
}
