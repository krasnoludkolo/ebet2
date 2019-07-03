package pl.krasnoludkolo.ebet2.user;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GlobalRoleTest {

    @Test
    public void shouldAdminContainsUser() {
        GlobalRole r = GlobalRole.SUPER_ADMIN;
        assertTrue(r.containsRole(GlobalRole.USER));
    }
}