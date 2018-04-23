package pl.krasnoludkolo.ebet2.bet.api;

public class BetAlreadySet extends RuntimeException {
    public BetAlreadySet(String msg) {
        super(msg);
    }
}
