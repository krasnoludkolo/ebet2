package pl.krasnoludkolo.ebet2.results;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
class UserResultEntity {

    @Id
    private String name;
    private int pointCounter;
    @ManyToOne(fetch = FetchType.EAGER)
    private RoundResultsEntity roundResultsEntity;

}
