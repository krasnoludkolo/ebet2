package pl.krasnoludkolo.ebet2.domain.league.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewMatchDTO {

    private String host;
    private String guest;
    private int round;

}
