package pl.krasnoludkolo.ebet2.user.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserInfo {

    private String username;
    private String password;

}
