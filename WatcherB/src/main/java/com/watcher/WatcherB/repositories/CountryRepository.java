package com.watcher.WatcherB.repositories;

import com.watcher.WatcherB.models.Data.Goods.Details.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Integer> {
}
