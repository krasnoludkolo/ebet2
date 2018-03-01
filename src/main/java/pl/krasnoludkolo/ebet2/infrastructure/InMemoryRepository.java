package pl.krasnoludkolo.ebet2.infrastructure;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.domain.Repository;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRepository<T> implements Repository<T> {

    private ConcurrentHashMap<UUID, T> map = new ConcurrentHashMap<>();

    @Override
    public void save(UUID uuid, T t) {
        map.put(uuid, t);
    }

    @Override
    public Option<T> findOne(UUID uuid) {
        return Option.of(map.get(uuid));
    }

    @Override
    public List<T> findAll() {
        return List.ofAll(map.values());
    }

    @Override
    public void delete(UUID uuid) {
        map.remove(uuid);
    }

    @Override
    public void update(UUID uuid, T t) {
        map.put(uuid, t);
    }

}
