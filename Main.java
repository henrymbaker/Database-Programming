/* Creates an instance of RegistrationSystem class and runs it with
 * the given userID, userPassword, and connection string.
*/
public class Main {
    public static void main(String[] args) {
        RegistrationSystem hw = new RegistrationSystem(args[0], args[1], args[2]);
        hw.run();
    }
}
