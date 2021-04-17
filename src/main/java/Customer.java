import java.util.Objects;

public class Customer {
    private final String firstName;
    private final String lastName;
    private final String PAN;

    Customer(String firstName, String lastName, String pan) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.PAN = pan;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPAN() {
        return PAN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return getFirstName().equals(customer.getFirstName()) && getLastName().equals(customer.getLastName()) && getPAN().equals(customer.getPAN());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getPAN());
    }

    @Override
    public String toString() {
        return "Customer{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", PAN='" + PAN + '\'' +
                '}';
    }
}