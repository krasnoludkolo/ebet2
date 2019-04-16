package pl.krasnoludkolo.ebet2.bet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
class BetEntity {

    @Id
    private UUID uuid;
    private UUID matchUuid;
    private UUID userUUID;
    private BetTyp betTyp;

}
