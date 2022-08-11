package by.kufar.bot.service;

import by.kufar.bot.entity.Advertisement;
import by.kufar.bot.entity.SearchRequest;
import by.kufar.bot.entity.User;

import java.util.List;

public interface AdvertisementService {

    long findQuantity(String query);
    List<Advertisement> findAll(String query);

    SearchRequest registerSearch(String query, User user);

    List<SearchRequest> findUserSearches(User user);

    void updateSearchResults();
}
