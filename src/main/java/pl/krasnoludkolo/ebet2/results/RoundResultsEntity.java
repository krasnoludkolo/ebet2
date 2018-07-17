package pl.krasnoludkolo.ebet2.results;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
class RoundResultsEntity {

    @Id
    private UUID uuid;
    private UUID leagueUUID;
    @OneToMany(mappedBy = "leagueResultsEntity", cascade = CascadeType.ALL)
    private List<UserResultEntity> userResultList;
    private int round;

}
