package pl.krasnoludkolo.ebet2.infrastructure;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class JOOQDatabaseConnector<E, D> implements Repository<D> {

    private final static Logger LOGGER = Logger.getLogger(JOOQDatabaseConnector.class.getName());


    private String username;
    private String password;
    private String url;

    private Function<D, E> domainToEntityMapper;
    private Function<E, D> entityToDomainMapper;

    public JOOQDatabaseConnector(Function<D, E> domainToEntityMapper, Function<E, D> entityToDomainMapper) {
        this.domainToEntityMapper = domainToEntityMapper;
        this.entityToDomainMapper = entityToDomainMapper;
        loadEnvironmentValues();
    }

    private void loadEnvironmentValues() {
        this.username = System.getenv("DATA_USER");
        this.password = System.getenv("DATA_PASSWORD");
        this.url = System.getenv("DATA_URL") + "/ebet2";
    }


    @Override
    public void save(UUID uuid, D d) {
        E entity = domainToEntityMapper.apply(d);
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            saveQuery(create, entity);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }

    }

    @Override
    public Option<D> findOne(UUID uuid) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            Result<Record> result = findOneQuery(create, uuid);
            E entity = convertRecordToEntity(result.get(0));
            D domain = entityToDomainMapper.apply(entity);
            return Option.of(domain);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return Option.none();
    }


    @Override
    public List<D> findAll() {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            Result<Record> result = findAllQuery(create);
            return convertToBetList(result);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return List.empty();
    }


    private List<D> convertToBetList(Result<Record> result) {
        List<D> resultList = List.empty();
        for (Record record : result) {
            E entity = convertRecordToEntity(record);
            D domain = entityToDomainMapper.apply(entity);
            resultList = resultList.append(domain);
        }
        return resultList;
    }


    @Override
    public void delete(UUID uuid) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            deleteQuery(create, uuid);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }


    @Override
    public void update(UUID uuid, D d) {
        E entity = domainToEntityMapper.apply(d);
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            updateQuery(create, entity, uuid);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    protected abstract void saveQuery(DSLContext create, E entity);

    protected abstract Result<Record> findOneQuery(DSLContext create, UUID uuid);

    protected abstract E convertRecordToEntity(Record record);

    protected abstract Result<Record> findAllQuery(DSLContext create);

    protected abstract void deleteQuery(DSLContext create, UUID uuid);

    protected abstract void updateQuery(DSLContext create, E entity, UUID uuid);

}
