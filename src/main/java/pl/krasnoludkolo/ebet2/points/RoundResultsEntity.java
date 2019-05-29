package pl.krasnoludkolo.ebet2.points;

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
    private UUID uuid;
    @OneToMany(cascade = CascadeType.ALL)
    private List<UserResultEntity> userResultList;
    private int round;

    RoundResultsEntity(List<UserResultEntity> userResultList, int round) {
        this.uuid = UUID.randomUUID();
        this.userResultList = userResultList;
        this.round = round;
    }

}