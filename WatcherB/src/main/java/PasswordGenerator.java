import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123"; // Пароль в открытом виде
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}