package pl.krasnoludkolo.ebet2.league;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
class LeagueEntity {

    @Id
    private UUID uuid;
    private String name;
    @OneToMany
    private List<MatchEntity> matches;


}
