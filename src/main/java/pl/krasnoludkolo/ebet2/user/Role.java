package pl.krasnoludkolo.ebet2.user;

import io.vavr.collection.List;

enum Role {
    USER(),
    ADMIN(USER);

    private List<Role> subRoles;

    Role() {
        this.subRoles = List.empty();
    }

    Role(Role... subRoles) {
        this.subRoles = List.of(subRoles);
    }

    boolean containsRole(Role role) {
        return this == role || subRoles.find(r -> r.containsRole(role)).isDefined();
    }
}
