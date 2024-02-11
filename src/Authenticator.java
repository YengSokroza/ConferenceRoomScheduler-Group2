import java.util.ArrayList;
import java.util.List;

public class Authenticator {
    private static final List<User> admins = new ArrayList<>();

    static {
        admins.add(new User("roza", "$1234"));
        admins.add(new User("admin", "$1234"));
    }

    public static boolean authenticate(String username, String password) {
        for (User admin : admins) {
            if (admin.getUsername().equals(username) && admin.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    private static class User {
        private final String username;
        private final String password;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
