package pl.krasnoludkolo.ebet2.bet.query;

import io.vavr.collection.List;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.infrastructure.ListResultJOOQQuery;

import java.util.UUID;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class BetForUserToMatchQuery extends ListResultJOOQQuery<BetDTO> {

    private UUID matchUUID;
    private String username;
    private ModelMapper modelMapper;

    public BetForUserToMatchQuery(UUID matchUUID, String username) {
        this.matchUUID = matchUUID;
        this.username = username;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.UNDERSCORE);
        modelMapper.getConfiguration().addValueReader(new RecordValueReader());
    }

    @Override
    protected SelectConditionStep<Record> query(DSLContext create) {
        return create
                .selectFrom(table("bet_entity"))
                .where(field("match_uuid").eq(matchUUID))
                .and(field("username").eq(username));
    }

    @Override
    protected List<BetDTO> mapResult(Result<Record> result) {
        return List
                .ofAll(result)
                .map(this::mapToBetDTO);
    }

    private BetDTO mapToBetDTO(Record record) {
        BetDTO betDTO = modelMapper.map(record, BetDTO.class);
        Integer betType = (Integer) record.getValue(1);
        betDTO.setBetTyp(BetTyp.values()[betType]);
        return betDTO;
    }
}
