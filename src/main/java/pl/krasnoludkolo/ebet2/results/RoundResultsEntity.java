package pl.krasnoludkolo.ebet2.results;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
class RoundResultsEntity {

    @Id
    private String uuid;
    private UUID leagueUUID;
    @OneToMany(mappedBy = "roundResultsEntity", cascade = CascadeType.ALL)
    private List<UserResultEntity> userResultList;
    private int round;

    RoundResultsEntity(UUID leagueUUID, List<UserResultEntity> userResultList, int round, String username) {
        this.leagueUUID = leagueUUID;
        this.userResultList = userResultList;
        this.round = round;
        this.uuid = leagueUUID.toString() + "_" + username + "_" + round;
    }

}
