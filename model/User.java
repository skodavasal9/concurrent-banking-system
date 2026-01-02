package BankingSystem.model;


public class User {

    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNumber;
    private final String userId;
    private final String password;

    private User(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.userId = builder.userId;
        this.password = builder.password;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getUserId() { return userId; }
    public String getPassword() { return password; }

    public static class Builder {
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String userId;
        private String password;

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public User build() {
            if (userId == null || password == null) {
                throw new IllegalStateException("userId and password are required");
            }
            return new User(this);
        }
    }
}
