package pl.krasnoludkolo.ebet2.infrastructure;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class JOOQDatabaseConnector<E, D> implements Repository<D> {

    private static final Logger LOGGER = Logger.getLogger(JOOQDatabaseConnector.class.getName());

    private DatabaseConnectionInfo dbConnectionInfo = new DatabaseConnectionInfo();
    private Function<D, E> domainToEntityMapper;
    private Function<E, D> entityToDomainMapper;
    protected ModelMapper modelMapper;

    public JOOQDatabaseConnector(Function<D, E> domainToEntityMapper, Function<E, D> entityToDomainMapper) {
        this.domainToEntityMapper = domainToEntityMapper;
        this.entityToDomainMapper = entityToDomainMapper;
        createModelMapper();
    }

    private void createModelMapper() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.UNDERSCORE);
        modelMapper.getConfiguration().addValueReader(new RecordValueReader());
    }

    @Override
    public void save(UUID uuid, D d) {
        E entity = domainToEntityMapper.apply(d);
        try (Connection connection = dbConnectionInfo.createConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            saveQuery(create, entity);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }

    }

    @Override
    public Option<D> findOne(UUID uuid) {
        try (Connection connection = dbConnectionInfo.createConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            return Option.of(findOneQuery(create, uuid))
                    .filter(Result::isNotEmpty)
                    .map(r -> r.get(0))
                    .map(this::convertRecordToEntity)
                    .map(entityToDomainMapper);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return Option.none();
    }


    @Override
    public List<D> findAll() {
        try (Connection connection = dbConnectionInfo.createConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            Result<Record> result = findAllQuery(create);
            return convertResultToDomainList(result);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }


    private List<D> convertResultToDomainList(Result<Record> result) {
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
        try (Connection connection = dbConnectionInfo.createConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            deleteQuery(create, uuid);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }


    @Override
    public void update(UUID uuid, D d) {
        E entity = domainToEntityMapper.apply(d);
        try (Connection connection = dbConnectionInfo.createConnection()) {
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
