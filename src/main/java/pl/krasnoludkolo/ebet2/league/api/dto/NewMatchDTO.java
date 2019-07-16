package pl.krasnoludkolo.ebet2.league.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewMatchDTO {

    private String host;
    private String guest;
    private int round;
    private UUID leagueUUID;
    private LocalDateTime matchStartDate;

}
