package pl.krasnoludkolo.ebet2.points;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RoundResultsEntity> roundResultsEntityList;


}
