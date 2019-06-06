package pl.krasnoludkolo.ebet2.points;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
class RoundResultsEntity {

    @Id
    private UUID uuid;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserResultEntity> userResultList;
    private int round;

    RoundResultsEntity(List<UserResultEntity> userResultList, int round) {
        this.uuid = UUID.randomUUID();
        this.userResultList = userResultList;
        this.round = round;
    }

}
