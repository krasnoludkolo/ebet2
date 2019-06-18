package pl.krasnoludkolo.ebet2.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class UserEntity {

    @Id
    private UUID uuid;
    private String username;
    private String password;
    private Role globalRole;

}
