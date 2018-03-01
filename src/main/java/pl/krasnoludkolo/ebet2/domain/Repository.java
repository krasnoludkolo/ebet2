package pl.krasnoludkolo.ebet2.domain;

import io.vavr.collection.List;
import io.vavr.control.Option;

import java.util.UUID;

public interface Repository<T> {

    void save(UUID uuid, T t);

    Option<T> findOne(UUID uuid);

    List<T> findAll();

    void delete(UUID uuid);

    void update(UUID uuid, T t);

}
