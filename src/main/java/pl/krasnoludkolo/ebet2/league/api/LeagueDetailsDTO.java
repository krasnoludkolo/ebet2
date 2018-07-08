package pl.krasnoludkolo.ebet2.league.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class LeagueDetailsDTO {

    private UUID leagueUUID;
    private String name;
    private boolean archived;

}
