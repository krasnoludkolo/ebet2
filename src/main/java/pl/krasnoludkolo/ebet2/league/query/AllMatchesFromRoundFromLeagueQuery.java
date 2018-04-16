package pl.krasnoludkolo.ebet2.league.query;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;
import pl.krasnoludkolo.ebet2.infrastructure.ResultJOOQQuery;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class AllMatchesFromRoundFromLeagueQuery extends ResultJOOQQuery<MatchDTO> {

    private UUID leagueUUID;
    private int round;
    private ModelMapper modelMapper;

    public AllMatchesFromRoundFromLeagueQuery(UUID leagueUUID, int round) {
        this.leagueUUID = leagueUUID;
        this.round = round;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.UNDERSCORE);
        modelMapper.getConfiguration().addValueReader(new RecordValueReader());
    }

    @Override
    protected Result<Record> query(DSLContext create) {
        return create
                .selectFrom(table("match"))
                .where(field("league_uuid").eq(leagueUUID))
                .and(field("round").eq(round))
                .fetch();
    }

    @Override
    protected List<MatchDTO> mapResult(Result<Record> result) {
        List<MatchDTO> resultList = new ArrayList<>();
        for (Record record : result) {
            MatchDTO matchDTO = modelMapper.map(record, MatchDTO.class);
            Integer matchResult = (Integer) record.getValue(3);
            matchDTO.setResult(MatchResult.values()[matchResult]);
            resultList.add(matchDTO);
        }
        return resultList;
    }
}
