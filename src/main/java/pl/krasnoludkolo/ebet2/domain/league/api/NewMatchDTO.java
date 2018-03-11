package pl.krasnoludkolo.ebet2.domain.league.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class NewMatchDTO {

    private String host;
    private String guest;
    private int round;
    private UUID leagueUUID;

}
