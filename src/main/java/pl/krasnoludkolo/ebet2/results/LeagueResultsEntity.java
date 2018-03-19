package pl.krasnoludkolo.ebet2.results;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@Getter
@Setter
class LeagueResultsEntity {

    @Id
    private UUID leagueUUID;
    @OneToMany(mappedBy = "leagueResultsEntity")
    private List<UserResultEntity> userResultList;

}
