package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;

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
        Either<String, String> token = userFacade.registerUser(username, password);
        assertTrue(token.isRight());
        String s = token.get();
        String[] split = s.split("\\.");
        assertEquals(3, split.length);
        Arrays.asList(split).forEach(part -> assertTrue(part.length() > 0));
    }

    @Test
    public void shouldNotRegisterUserWithUsedUsername() {
        userFacade.registerUser(username, password);
        Either<String, String> token = userFacade.registerUser(username, password);
        assertTrue(token.isLeft());
        assertEquals("Duplicate username", token.getLeft());
    }

    @Test
    public void shouldNotRegisterUserWithEmptyPassword() {
        Either<String, String> token = userFacade.registerUser(username, "");
        assertTrue(token.isLeft());
        assertEquals("Empty password", token.getLeft());
    }

    @Test
    public void shouldNotRegisterUserWithEmptyUsername() {
        Either<String, String> token = userFacade.registerUser("", password);
        assertTrue(token.isLeft());
        assertEquals("Empty username", token.getLeft());
    }

    @Test
    public void shouldNotRegisterUserWithNullPassword() {
        Either<String, String> token = userFacade.registerUser(username, null);
        assertTrue(token.isLeft());
        assertEquals("Empty password", token.getLeft());
    }

    @Test
    public void shouldNotRegisterUserWithNullUsername() {
        Either<String, String> token = userFacade.registerUser(null, password);
        assertTrue(token.isLeft());
        assertEquals("Empty username", token.getLeft());
    }

    @Test
    public void shouldNotGenerateTokenToUserWithWrongPassword() {
        //given
        userFacade.registerUser(username, password);
        //when
        Either<String, String> token = userFacade.generateToken(username, "wrongpassword");
        //then
        assertTrue(token.isLeft());
        assertEquals("Wrong password", token.getLeft());
    }

    @Test
    public void shouldGenerateTokenToRegisteredUser() {
        //given
        userFacade.registerUser(username, password);
        //when
        Either<String, String> token = userFacade.generateToken(username, password);
        //then
        assertTrue(token.isLeft());
    }

    @Test
    public void shouldGetUsernameFromToken() {
        //given
        String registrationToken = userFacade.registerUser(username, password).get();
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