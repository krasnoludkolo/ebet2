package pl.krasnoludkolo.ebet2.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
class LeagueDetails {


    @Id
    private UUID leagueUUID;
    @OneToMany(cascade = CascadeType.ALL)
    private List<LeagueDetailsSetting> config;
    private String clientShortcut;

}
