package pl.krasnoludkolo.ebet2.domain.bet.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewBetDTO {

    private final BetTyp betTyp;
    private final String username;


}
