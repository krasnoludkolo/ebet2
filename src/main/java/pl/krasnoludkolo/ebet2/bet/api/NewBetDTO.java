package pl.krasnoludkolo.ebet2.bet.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewBetDTO {

    private BetTyp betTyp;
    private String username;
    private UUID matchUUID;

}
