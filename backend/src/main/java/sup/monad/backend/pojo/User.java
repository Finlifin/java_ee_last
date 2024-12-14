package sup.monad.backend.pojo;

import jakarta.persistence.Table;

@Table(name = "users")
public class User {
    private Long id;
    private String password;
    private String email;
    private String role;

    public User() {
    }

    public User(Long id, String password, String email, String role) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}