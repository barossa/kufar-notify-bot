package by.kufar.bot.repo;

import by.kufar.bot.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementsRepository extends JpaRepository<Advertisement, Long> {
    List<Advertisement> findAdvertisementsByIdIn(List<Long> ids);
}
