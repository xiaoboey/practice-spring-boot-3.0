package top.xiaoboey.practice.spring.boot3.simplestarter.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.xiaoboey.practice.spring.boot3.simplestarter.entity.SimpleUser;

/**
 * @author xiaoqb
 */
public interface SimpleUserRepository extends JpaRepository<SimpleUser, Long> {
    SimpleUser findByUsername(String username);
}
