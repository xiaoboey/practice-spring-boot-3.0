package top.xiaoboey.practice.single.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.xiaoboey.practice.single.entity.SimpleUser;

/**
 * @author xiaoqb
 */
public interface SimpleUserRepository extends JpaRepository<SimpleUser, Long> {
    SimpleUser findByUsername(String username);
}
