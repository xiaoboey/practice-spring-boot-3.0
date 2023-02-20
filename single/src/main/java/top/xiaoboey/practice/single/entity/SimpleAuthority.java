package top.xiaoboey.practice.single.entity;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.*;

/**
 * @author xiaoqb
 */
@Entity
@Table(name = "simple_authority")
public class SimpleAuthority implements GrantedAuthority {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    public SimpleAuthority() {
    }

    public SimpleAuthority(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getAuthority() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
