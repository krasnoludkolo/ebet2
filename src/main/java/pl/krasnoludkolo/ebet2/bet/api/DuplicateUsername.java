package pl.krasnoludkolo.ebet2.bet.api;

public class DuplicateUsername extends RuntimeException {
    public DuplicateUsername(String s) {
        super(s);
    }
}
