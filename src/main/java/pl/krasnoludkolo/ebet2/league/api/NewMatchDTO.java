package pl.krasnoludkolo.ebet2.league.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class NewMatchDTO {

    private final String host;
    private final String guest;
    private final int round;
    private final UUID leagueUUID;

}
