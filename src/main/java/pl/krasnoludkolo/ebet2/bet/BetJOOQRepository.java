package pl.krasnoludkolo.ebet2.bet;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import pl.krasnoludkolo.ebet2.bet.api.BetType;
import pl.krasnoludkolo.ebet2.infrastructure.JOOQDatabaseConnector;

import java.util.UUID;
import java.util.function.Function;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

class BetJOOQRepository extends JOOQDatabaseConnector<BetEntity, Bet> {

    private static final String BET_ENTITY = "bet_entity";

    BetJOOQRepository(Function<Bet, BetEntity> d2e, Function<BetEntity, Bet> e2d) {
        super(d2e, e2d);
    }

    @Override
    protected void saveQuery(DSLContext create, BetEntity entity) {
        create
                .insertInto(table(BET_ENTITY))
                .columns(field("uuid"), field("bet_type"), field("match_uuid"), field("userUUID"))
                .values(entity.getUuid(), entity.getBetType().ordinal(), entity.getMatchUuid(), entity.getUserUUID())
                .execute();
    }

    @Override
    protected Result<Record> findOneQuery(DSLContext create, UUID uuid) {
        return create
                .select()
                .from(table(BET_ENTITY))
                .where(field("uuid").eq(uuid))
                .limit(1)
                .fetch();
    }

    @Override
    protected BetEntity convertRecordToEntity(Record record) {
        BetEntity entity = modelMapper.map(record, BetEntity.class);
        Integer betTyp = (Integer) record.getValue(1);
        entity.setBetType(BetType.values()[betTyp]);
        return entity;
    }

    @Override
    protected Result<Record> findAllQuery(DSLContext create) {
        return create
                .select().from(table(BET_ENTITY))
                .fetch();
    }

    @Override
    protected void deleteQuery(DSLContext create, UUID uuid) {
        create
                .deleteFrom(table(BET_ENTITY))
                .where(field("uuid").eq(uuid))
                .execute();
    }

    @Override
    protected void updateQuery(DSLContext create, BetEntity entity, UUID uuid) {
        create
                .update(table(BET_ENTITY))
                .set(field("bet_type"), entity.getBetType().ordinal())
                .set(field("match_uuid"), entity.getMatchUuid())
                .set(field("userUUID"), entity.getUserUUID())
                .where(field("uuid").eq(uuid))
                .execute();
    }
}
