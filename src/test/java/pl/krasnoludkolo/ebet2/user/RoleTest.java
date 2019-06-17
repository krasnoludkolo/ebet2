package pl.krasnoludkolo.ebet2.user;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RoleTest {

    @Test
    public void shouldAdminContainsUser() {
        Role r = Role.ADMIN;
        assertTrue(r.containsRole(Role.USER));
    }
}