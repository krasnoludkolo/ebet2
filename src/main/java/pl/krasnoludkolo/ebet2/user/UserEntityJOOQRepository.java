package pl.krasnoludkolo.ebet2.user;

import org.jooq.*;
import pl.krasnoludkolo.ebet2.infrastructure.JOOQDatabaseConnector;

import java.util.UUID;
import java.util.function.Function;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

final class UserEntityJOOQRepository extends JOOQDatabaseConnector<UserEntity, UserEntity> {

    private static final Table<Record> USER_ENTITY = table("user_entity");
    private static final Field<UUID> UUID = field("uuid",UUID.class);
    private static final Field<String> USERNAME = field("username",String.class);
    private static final Field<String> PASSWORD = field("password",String.class);
    private static final Field<Integer> GLOBAL_ROLE = field("global_role",Integer.class);

    UserEntityJOOQRepository(Function<UserEntity, UserEntity> domainToEntityMapper, Function<UserEntity, UserEntity> entityToDomainMapper) {
        super(domainToEntityMapper, entityToDomainMapper);
    }

    @Override
    protected void saveQuery(DSLContext create, UserEntity entity) {
        create
                .insertInto(USER_ENTITY)
                .columns(UUID, USERNAME, PASSWORD, GLOBAL_ROLE)
                .values(entity.getUuid(), entity.getUsername(), entity.getPassword(), entity.getGlobalRole().ordinal())
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
        int role = record.get(GLOBAL_ROLE);
        entity.setGlobalRole(GlobalRole.values()[role]);
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
                .set(USERNAME, entity.getUsername())
                .set(PASSWORD, entity.getPassword())
                .set(GLOBAL_ROLE, entity.getGlobalRole().ordinal())
                .where(UUID.eq(uuid))
                .execute();
    }

}
