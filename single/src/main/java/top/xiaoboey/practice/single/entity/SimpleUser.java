package top.xiaoboey.practice.single.entity;

import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author xiaoqb
 */
@Entity
@Table(name = "simple_user")
public class SimpleUser implements UserDetails, Serializable {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String password;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id", table = "simple_authority"))
    private Collection<SimpleAuthority> authorities;

    public SimpleUser() {
    }

    public SimpleUser(long id, String username, String password) {
        this.id = id;
        this.setUsername(username);
        this.setPassword(password);
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Collection<SimpleAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<SimpleAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
