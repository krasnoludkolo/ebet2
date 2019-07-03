package pl.krasnoludkolo.ebet2.user;

import io.vavr.collection.List;

enum GlobalRole {
    USER(),
    SUPER_ADMIN(USER);

    private List<GlobalRole> subRoles;

    GlobalRole() {
        this.subRoles = List.empty();
    }

    GlobalRole(GlobalRole... subGlobalRoles) {
        this.subRoles = List.of(subGlobalRoles);
    }

    boolean containsRole(GlobalRole globalRole) {
        return this == globalRole || subRoles.find(r -> r.containsRole(globalRole)).isDefined();
    }
}
