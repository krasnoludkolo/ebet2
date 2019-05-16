package pl.krasnoludkolo.ebet2.update;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import pl.krasnoludkolo.ebet2.infrastructure.JOOQDatabaseConnector;

import java.util.UUID;
import java.util.function.Function;

final class JOOQUpdateDetailsRepository extends JOOQDatabaseConnector<UpdateDetails, UpdateDetails> {

    JOOQUpdateDetailsRepository(Function<UpdateDetails, UpdateDetails> domainToEntityMapper, Function<UpdateDetails, UpdateDetails> entityToDomainMapper) {
        super(domainToEntityMapper, entityToDomainMapper);
    }

    @Override
    protected void saveQuery(DSLContext create, UpdateDetails entity) {

    }

    @Override
    protected Result<Record> findOneQuery(DSLContext create, UUID uuid) {
        return null;
    }

    @Override
    protected UpdateDetails convertRecordToEntity(Record record) {
        return null;
    }

    @Override
    protected Result<Record> findAllQuery(DSLContext create) {
        return null;
    }

    @Override
    protected void deleteQuery(DSLContext create, UUID uuid) {

    }

    @Override
    protected void updateQuery(DSLContext create, UpdateDetails entity, UUID uuid) {

    }
}
