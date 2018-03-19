package pl.krasnoludkolo.ebet2.bet.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewBetDTO {

    private BetTyp betTyp;
    private String username;
    private UUID matchUUID;

}