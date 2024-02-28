package hu.bme.aut.stepsysterv.PooBer.users.data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * USER ENTITY class
 * This class contains all the information stored in a user entity
 * The following are stored:
 * User id, username, password, email address, banned status(cannot book new WC)
 */
@Entity
public class User {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(unique=true, nullable = false)
    @NotBlank(message = "Username is mandatory")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Password is mandatory")
    @Size(min = 3, message = "Min password length is 3")
    private String password;

    @Column(nullable = false)
    @NotBlank(message = "E-mail address is mandatory")
    @Email
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    private boolean banned = false;

    //Usual setters and getter

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}