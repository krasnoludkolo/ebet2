package pl.krasnoludkolo.ebet2.user;

import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.user.api.LoginUserInfo;
import pl.krasnoludkolo.ebet2.user.api.PromoteToSuperAdminRequest;
import pl.krasnoludkolo.ebet2.user.api.UserDetails;
import pl.krasnoludkolo.ebet2.user.api.UserError;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.*;

public class UserFacadeTest {

    private UserFacade userFacade;

    private String username = "username";
    private String password = "password";
    private LoginUserInfo loginUserInfo = new LoginUserInfo(username, password);

    @Before
    public void init() {
        InMemorySystem system = new InMemorySystem();
        userFacade = system.userFacade();
    }

    @Test
    public void shouldRegisterUser() {
        String token = userFacade.registerUser(loginUserInfo).get().getToken();
        String[] split = token.split("\\.");
        assertEquals(3, split.length);
        Arrays.asList(split).forEach(part -> assertTrue(part.length() > 0));
    }

    @Test
    public void shouldNotRegisterUserWithUsedUsername() {
        userFacade.registerUser(loginUserInfo);
        UserError token = userFacade.registerUser(loginUserInfo).getLeft();
        assertEquals(UserError.DUPLICATED_USERNAME, token);
    }

    @Test
    public void shouldNotRegisterUserWithEmptyPassword() {
        UserError token = userFacade.registerUser(new LoginUserInfo(username, "")).getLeft();
        assertEquals(UserError.EMPTY_USERNAME_OR_PASSWORD, token);
    }

    @Test
    public void shouldNotRegisterUserWithEmptyUsername() {
        UserError token = userFacade.registerUser(new LoginUserInfo("", password)).getLeft();
        assertEquals(UserError.EMPTY_USERNAME_OR_PASSWORD, token);
    }

    @Test
    public void shouldNotRegisterUserWithNullPassword() {
        UserError token = userFacade.registerUser(new LoginUserInfo(username, null)).getLeft();
        assertEquals(UserError.EMPTY_USERNAME_OR_PASSWORD, token);
    }

    @Test
    public void shouldNotRegisterUserWithNullUsername() {
        UserError token = userFacade.registerUser(new LoginUserInfo(null, password)).getLeft();
        assertEquals(UserError.EMPTY_USERNAME_OR_PASSWORD, token);
    }

    @Test
    public void shouldNotGenerateTokenToUserWithWrongPassword() {
        //given
        userFacade.registerUser(loginUserInfo);
        //when
        UserError token = userFacade.login(new LoginUserInfo(username, "wrongpassword")).getLeft();
        //then

        assertEquals(UserError.WRONG_PASSWORD, token);
    }

    @Test
    public void shouldGenerateTokenToRegisteredUser() {
        //given
        userFacade.registerUser(loginUserInfo);
        //when
        String token = userFacade.login(loginUserInfo).get().getToken();
        //then
        assertTrue(token.length() > 0);
    }

    @Test
    public void shouldReturnAllUserDetails() {
        //given
        UUID userUUID = userFacade.registerUser(loginUserInfo).get().getUserUUID();
        //when
        UserDetails details = userFacade.login(loginUserInfo).get();
        //then
        assertEquals(this.username, details.getUsername());
        assertEquals(userUUID, details.getUserUUID());
    }

    @Test
    public void shouldGetUsernameByUUID() {
        //given
        UUID uuid = userFacade.registerUser(loginUserInfo).get().getUserUUID();
        //when
        String username = userFacade.getUsernameFromUUID(uuid).get();
        //then
        assertEquals(this.username, username);
    }

    @Test
    public void shouldGetUUIDByToken() {
        //given
        UserDetails userDetails = userFacade.registerUser(loginUserInfo).get();
        String token = userDetails.getToken();
        UUID uuid = userDetails.getUserUUID();
        //when
        UUID testUUID = userFacade.getUserUUIDFromToken(token).get();
        //then
        assertEquals(uuid, testUUID);
    }

    @Test
    public void shouldNotGetUUIDByNoExistingToken() {
        //given
        //when
        UserError error = userFacade.getUserUUIDFromToken("No existing").getLeft();
        //then
        assertEquals(UserError.WRONG_TOKEN, error);
    }

    @Test
    public void shouldNotGetUsernameByWrongUUID() {
        //when
        UserError error = userFacade.getUsernameFromUUID(UUID.randomUUID()).getLeft();
        //then
        assertEquals(UserError.UUID_NOT_FOUND, error);
    }

    @Test
    public void shouldReturnGivenByRegisterUsername() {
        //when
        String username = userFacade.registerUser(loginUserInfo).get().getUsername();
        //then
        assertEquals(this.username, username);
    }

    @Test
    public void shouldReturnUUIDInResponse() {
        //when
        UUID userUUID = userFacade.registerUser(loginUserInfo).get().getUserUUID();
        //then
        assertNotNull(userUUID);
    }

    @Test
    public void shouldCreateNewUserWithOutSuperUserRole() {
        //given
        UUID userUUID = userFacade.registerUser(loginUserInfo).get().getUserUUID();
        //when
        Boolean isAdmin = userFacade.isSuperAdmin(userUUID).get();
        //then
        assertFalse(isAdmin);
    }

    @Test
    public void shouldCreateNewSuperAdminWithSuperUserRole() {
        //given
        UUID userUUID = userFacade.registerSuperAdmin(loginUserInfo).get().getUserUUID();
        //when
        Boolean isAdmin = userFacade.isSuperAdmin(userUUID).get();
        //then
        assertTrue(isAdmin);
    }

    @Test
    public void shouldSuperAdminPromoteNormalUserToSuperAdmin() {
        //given
        UUID admin = userFacade.registerSuperAdmin(loginUserInfo).get().getUserUUID();
        UUID user = userFacade.registerUser(loginUserInfo).get().getUserUUID();
        //when
        userFacade.promoteToSuperAdmin(new PromoteToSuperAdminRequest(user, admin));
        //then
        assertTrue(userFacade.isSuperAdmin(user).get());
    }

    @Test
    public void shouldNotNormalUserPromoteNormalUserToSuperAdmin() {
        //given
        UUID admin = userFacade.registerSuperAdmin(loginUserInfo).get().getUserUUID();
        UUID user = userFacade.registerUser(loginUserInfo).get().getUserUUID();
        //when
        UserError error = userFacade.promoteToSuperAdmin(new PromoteToSuperAdminRequest(user, admin)).getLeft();
        //then
        assertEquals(UserError.NOT_REQUIRED_ROLE, error);
    }

}