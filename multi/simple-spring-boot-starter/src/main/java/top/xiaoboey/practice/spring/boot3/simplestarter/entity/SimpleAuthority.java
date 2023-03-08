package top.xiaoboey.practice.spring.boot3.simplestarter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;

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
