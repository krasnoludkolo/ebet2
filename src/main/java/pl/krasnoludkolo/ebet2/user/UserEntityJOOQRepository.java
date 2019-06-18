package pl.krasnoludkolo.ebet2.user;

import org.jooq.*;
import pl.krasnoludkolo.ebet2.infrastructure.JOOQDatabaseConnector;

import java.util.UUID;
import java.util.function.Function;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

final class UserEntityJOOQRepository extends JOOQDatabaseConnector<UserEntity, UserEntity> {

    private static final Table<Record> USER_ENTITY = table("user_entity");
    private static final Field<Object> UUID = field("uuid");
    private static final Field<Object> USERNAME = field("username");
    private static final Field<Object> PASSWORD = field("password");

    UserEntityJOOQRepository(Function<UserEntity, UserEntity> domainToEntityMapper, Function<UserEntity, UserEntity> entityToDomainMapper) {
        super(domainToEntityMapper, entityToDomainMapper);
    }

    @Override
    protected void saveQuery(DSLContext create, UserEntity entity) {
        create
                .insertInto(USER_ENTITY)
                .columns(UUID, USERNAME, PASSWORD)
                .values(entity.getUuid(), entity.getUsername(), entity.getPassword())
                .execute();
    }

    @Override
    protected Result<Record> findOneQuery(DSLContext create, UUID uuid) {
        return create
                .select()
                .from(USER_ENTITY)
                .where(UUID.eq(uuid))
                .limit(1)
                .fetch();
    }

    @Override
    protected UserEntity convertRecordToEntity(Record record) {
        UserEntity entity = modelMapper.map(record, UserEntity.class);
        Integer role = (Integer) record.getValue(3);
        entity.setGlobalRole(Role.values()[role]);
        return entity;
    }

    @Override
    protected Result<Record> findAllQuery(DSLContext create) {
        return create
                .select().from(USER_ENTITY)
                .fetch();
    }

    @Override
    protected void deleteQuery(DSLContext create, UUID uuid) {
        create
                .deleteFrom(USER_ENTITY)
                .where(field(UUID).eq(uuid))
                .execute();

    }

    @Override
    protected void updateQuery(DSLContext create, UserEntity entity, UUID uuid) {
        create
                .update(USER_ENTITY)
                .set(field(USERNAME), entity.getUsername())
                .set(field(PASSWORD), entity.getPassword())
                .where(field(UUID).eq(uuid))
                .execute();
    }

}
