
/**
 * firstName and lastName is used later for credit card validation
 * id is used to insert a value into the sales table once a payment goes through
 */
public class User {

    private final String username;
    private final String firstName;
    private final String lastName;
    private final int id;

    public User(String email, String firstName, String lastName, int id) {
        this.username = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getId() {
        return id;
    }
}