package pl.krasnoludkolo.ebet2.results;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import pl.krasnoludkolo.ebet2.infrastructure.JOOQDatabaseConnector;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.function.Function;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

final class JOOQUpdateDetailsRepository extends JOOQDatabaseConnector<UpdateDetailsEntity, UpdateDetails> {

    private static final String TABLE_NAME = "update_details_entity";
    private static final String ID = "matchuuid";

    JOOQUpdateDetailsRepository(Function<UpdateDetails, UpdateDetailsEntity> domainToEntityMapper, Function<UpdateDetailsEntity, UpdateDetails> entityToDomainMapper) {
        super(domainToEntityMapper, entityToDomainMapper);
    }

    @Override
    protected void saveQuery(DSLContext create, UpdateDetailsEntity entity) {
        create
                .insertInto(table(TABLE_NAME))
                .columns(field(ID), field("attempt"), field("scheduled_time"))
                .values(entity.getMatchUUID(), entity.getAttempt(), entity.getScheduledTime())
                .execute();
    }

    @Override
    protected Result<Record> findOneQuery(DSLContext create, UUID uuid) {
        return create
                .select()
                .from(table(TABLE_NAME))
                .where(field(ID).eq(uuid))
                .limit(1)
                .fetch();
    }

    @Override
    protected UpdateDetailsEntity convertRecordToEntity(Record record) {
        UpdateDetailsEntity entity = modelMapper.map(record, UpdateDetailsEntity.class);
        String text = record.get(2).toString();
        Timestamp timestamp = Timestamp.valueOf(text);
        entity.setScheduledTime(LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault()));
        return entity;
    }

    @Override
    protected Result<Record> findAllQuery(DSLContext create) {
        return create
                .select().from(table(TABLE_NAME))
                .fetch();
    }

    @Override
    protected void deleteQuery(DSLContext create, UUID uuid) {
        create
                .deleteFrom(table(TABLE_NAME))
                .where(field(ID).eq(uuid))
                .execute();
    }

    @Override
    protected void updateQuery(DSLContext create, UpdateDetailsEntity entity, UUID uuid) {
        create
                .update(table(TABLE_NAME))
                .set(field("scheduled_time"), entity.getScheduledTime())
                .set(field("attempt"), entity.getAttempt())
                .where(field(ID).eq(uuid))
                .execute();
    }

}
