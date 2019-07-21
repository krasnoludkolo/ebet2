package pl.krasnoludkolo.ebet2.bet;

import org.jooq.*;
import pl.krasnoludkolo.ebet2.bet.api.BetType;
import pl.krasnoludkolo.ebet2.infrastructure.JOOQDatabaseConnector;

import java.util.UUID;
import java.util.function.Function;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

class BetJOOQRepository extends JOOQDatabaseConnector<BetEntity, Bet> {

    private static final Table<Record> BET_ENTITY = table("bet_entity");

    private static final Field<UUID> UUID = field("uuid",UUID.class);
    private static final Field<Integer> BET_TYPE = field("uuid",Integer.class);
    private static final Field<UUID> MATCH_UUID = field("uuid",UUID.class);
    private static final Field<UUID> USER_UUID = field("uuid",UUID.class);

    BetJOOQRepository(Function<Bet, BetEntity> d2e, Function<BetEntity, Bet> e2d) {
        super(d2e, e2d);
    }

    @Override
    protected void saveQuery(DSLContext create, BetEntity entity) {
        create
                .insertInto(BET_ENTITY)
                .columns(UUID, BET_TYPE, MATCH_UUID, USER_UUID)
                .values(entity.getUuid(), entity.getBetType().ordinal(), entity.getMatchUuid(), entity.getUserUUID())
                .execute();
    }

    @Override
    protected Result<Record> findOneQuery(DSLContext create, UUID uuid) {
        return create
                .select()
                .from(BET_ENTITY)
                .where(UUID.eq(uuid))
                .limit(1)
                .fetch();
    }

    @Override
    protected BetEntity convertRecordToEntity(Record record) {
        BetEntity entity = modelMapper.map(record, BetEntity.class);
        int betTyp = record.get(BET_TYPE);
        entity.setBetType(BetType.values()[betTyp]);
        return entity;
    }

    @Override
    protected Result<Record> findAllQuery(DSLContext create) {
        return create
                .select().from(BET_ENTITY)
                .fetch();
    }

    @Override
    protected void deleteQuery(DSLContext create, UUID uuid) {
        create
                .deleteFrom(BET_ENTITY)
                .where(UUID.eq(uuid))
                .execute();
    }

    @Override
    protected void updateQuery(DSLContext create, BetEntity entity, UUID uuid) {
        create
                .update(BET_ENTITY)
                .set(BET_TYPE, entity.getBetType().ordinal())
                .set(MATCH_UUID, entity.getMatchUuid())
                .set(USER_UUID, entity.getUserUUID())
                .where(UUID.eq(uuid))
                .execute();
    }
}
