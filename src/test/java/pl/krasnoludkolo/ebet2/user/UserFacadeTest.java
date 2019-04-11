package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.user.api.UserError;
import pl.krasnoludkolo.ebet2.user.api.UserInfo;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserFacadeTest {

    private UserFacade userFacade;

    private String username = "username";
    private String password = "password";

    @Before
    public void init() {
        InMemorySystem system = new InMemorySystem();
        userFacade = system.userFacade();
    }

    @Test
    public void shouldRegisterUser() {
        String token = userFacade.registerUser(new UserInfo(username, password)).get();
        String[] split = token.split("\\.");
        assertEquals(3, split.length);
        Arrays.asList(split).forEach(part -> assertTrue(part.length() > 0));
    }

    @Test
    public void shouldNotRegisterUserWithUsedUsername() {
        userFacade.registerUser(new UserInfo(username, password));
        UserError token = userFacade.registerUser(new UserInfo(username, password)).getLeft();
        assertEquals(UserError.DUPLICATED_USERNAME, token);
    }

    @Test
    public void shouldNotRegisterUserWithEmptyPassword() {
        UserError token = userFacade.registerUser(new UserInfo(username, "")).getLeft();
        assertEquals(UserError.EMPTY_USERNAME_OR_PASSWORD, token);
    }

    @Test
    public void shouldNotRegisterUserWithEmptyUsername() {
        UserError token = userFacade.registerUser(new UserInfo("", password)).getLeft();
        assertEquals(UserError.EMPTY_USERNAME_OR_PASSWORD, token);
    }

    @Test
    public void shouldNotRegisterUserWithNullPassword() {
        UserError token = userFacade.registerUser(new UserInfo(username, null)).getLeft();
        assertEquals(UserError.EMPTY_USERNAME_OR_PASSWORD, token);
    }

    @Test
    public void shouldNotRegisterUserWithNullUsername() {
        UserError token = userFacade.registerUser(new UserInfo(null, password)).getLeft();
        assertEquals(UserError.EMPTY_USERNAME_OR_PASSWORD, token);
    }

    @Test
    public void shouldNotGenerateTokenToUserWithWrongPassword() {
        //given
        userFacade.registerUser(new UserInfo(username, password));
        //when
        UserError token = userFacade.generateToken(new UserInfo(username, "wrongpassword")).getLeft();
        //then

        assertEquals(UserError.WRONG_PASSWORD, token);
    }

    @Test
    public void shouldGenerateTokenToRegisteredUser() {
        //given
        userFacade.registerUser(new UserInfo(username, password));
        //when
        String token = userFacade.generateToken(new UserInfo(username, password)).get();
        //then
        assertTrue(token.length() > 0);
    }

    @Test
    public void shouldGetUsernameFromToken() {
        //given
        String registrationToken = userFacade.registerUser(new UserInfo(username, password)).get();
        //when
        String user = userFacade.getUsername(registrationToken).get();
        //then
        assertEquals(username, user);
    }

    @Test
    public void shouldNotGetUsernameFromWrongToken() {
        //when
        Either<String, String> someWrongToken = userFacade.getUsername("SomeWrongToken");
        //then
        assertTrue(someWrongToken.isLeft());
        assertEquals("Wrong token", someWrongToken.getLeft());
    }

}