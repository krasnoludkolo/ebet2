package pl.krasnoludkolo.ebet2.results;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
class LeagueResultEntity {

    @Id
    private UUID leagueUUID;
    @OneToMany
    private List<RoundResultsEntity> roundResultsEntityList;


}
