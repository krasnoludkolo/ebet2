package pl.krasnoludkolo.ebet2.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class LeagueDetailsSetting {

    @Id
    private UUID uuid;
    private String name;
    private String value;

}
