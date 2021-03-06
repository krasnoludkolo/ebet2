package pl.krasnoludkolo.ebet2.league.query;

import io.vavr.collection.List;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;
import pl.krasnoludkolo.ebet2.infrastructure.ListResultJOOQQuery;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchResult;

import java.util.UUID;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class AllMatchesFromRoundFromLeagueQueryList extends ListResultJOOQQuery<MatchDTO> {

    private UUID leagueUUID;
    private int round;
    private ModelMapper modelMapper;

    public AllMatchesFromRoundFromLeagueQueryList(UUID leagueUUID, int round) {
        this.leagueUUID = leagueUUID;
        this.round = round;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.UNDERSCORE);
        modelMapper.getConfiguration().addValueReader(new RecordValueReader());
    }

    @Override
    protected SelectConditionStep<Record> query(DSLContext create) {
        return create
                .selectFrom(table("match"))
                .where(field("league_uuid").eq(leagueUUID))
                .and(field("round").eq(round));
    }

    @Override
    protected List<MatchDTO> mapResult(Result<Record> result) {
        return List
                .ofAll(result)
                .map(this::mapToMatchDTO);
    }

    private MatchDTO mapToMatchDTO(Record record) {
        MatchDTO matchDTO = modelMapper.map(record, MatchDTO.class);
        Integer matchResult = (Integer) record.getValue(3);
        matchDTO.setResult(MatchResult.values()[matchResult]);
        return matchDTO;
    }
}
