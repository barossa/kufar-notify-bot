package by.kufar.bot.repo;

import by.kufar.bot.entity.SearchRequest;
import by.kufar.bot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchRequestRepository extends JpaRepository<SearchRequest, Long> {
    List<SearchRequest> findSearchRequestsByUsersContaining(User user);

    Optional<SearchRequest> findSearchRequestByQueryEqualsIgnoreCase(String query);
}
