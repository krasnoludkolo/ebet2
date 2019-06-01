package pl.krasnoludkolo.ebet2.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
class LeagueDetails {


    @Id
    private UUID leagueUUID;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<LeagueDetailsSetting> config;
    private String clientShortcut;

}
