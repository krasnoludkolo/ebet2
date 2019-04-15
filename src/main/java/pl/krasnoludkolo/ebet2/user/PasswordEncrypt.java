package pl.krasnoludkolo.ebet2.user;

interface PasswordEncrypt {

    String encryptPassword(String password);

    boolean checkPassword(String candidate, String password);

}
