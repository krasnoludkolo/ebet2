package pl.krasnoludkolo.ebet2.bet;

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
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

class BetJOOQRepository implements Repository<Bet> {

    private final static Logger LOGGER = Logger.getLogger(BetJOOQRepository.class.getName());

    private final String BET_ENTITY = "bet_entity";

    private String username;
    private String password;
    private String url;
    private ModelMapper modelMapper;

    BetJOOQRepository() {
        loadEnvironmentValues();
        createModelMapper();
    }

    private void loadEnvironmentValues() {
        this.username = System.getenv("DATA_USER");
        this.password = System.getenv("DATA_PASSWORD");
        this.url = System.getenv("DATA_URL") + "/ebet2";
    }

    private void createModelMapper() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.UNDERSCORE);
        modelMapper.getConfiguration().addValueReader(new RecordValueReader());
    }

    @Override
    public void save(UUID uuid, Bet bet) {
        BetEntity entity = bet.toEntity();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            create
                    .insertInto(table(BET_ENTITY))
                    .columns(field("uuid"), field("bet_typ"), field("match_uuid"), field("username"))
                    .values(entity.getUuid(), entity.getBetTyp().ordinal(), entity.getMatchUuid(), entity.getUsername())
                    .execute();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    @Override
    public Option<Bet> findOne(UUID uuid) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            Result<Record> result = create
                    .select()
                    .from(table(BET_ENTITY))
                    .where(field("uuid").eq(uuid))
                    .limit(1)
                    .fetch();
            Bet bet = convertRecordToBet(result.get(0));
            return Option.of(bet);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return Option.none();
    }

    @Override
    public List<Bet> findAll() {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {

            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);

            Result<Record> result = create
                    .select().from(table(BET_ENTITY))
                    .fetch();
            return convertToBetList(result);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return List.empty();
    }

    private List<Bet> convertToBetList(Result<Record> result) {
        List<Bet> resultList = List.empty();
        for (Record record : result) {
            Bet bet = convertRecordToBet(record);
            resultList = resultList.append(bet);
        }
        return resultList;
    }

    private Bet convertRecordToBet(Record record) {
        BetEntity entity = modelMapper.map(record, BetEntity.class);
        Integer bet_typ = (Integer) record.getValue(1);
        entity.setBetTyp(BetTyp.values()[bet_typ]);
        return new Bet(entity);
    }

    @Override
    public void delete(UUID uuid) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            create
                    .deleteFrom(table(BET_ENTITY))
                    .where(field("uuid").eq(uuid))
                    .execute();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    @Override
    public void update(UUID uuid, Bet bet) {
        BetEntity entity = bet.toEntity();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            create
                    .update(table(BET_ENTITY))
                    .set(field("bet_type"), entity.getBetTyp().ordinal())
                    .set(field("match_uuid"), entity.getMatchUuid())
                    .set(field("username"), entity.getUsername())
                    .where(field("uuid").eq(uuid))
                    .execute();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }
}
