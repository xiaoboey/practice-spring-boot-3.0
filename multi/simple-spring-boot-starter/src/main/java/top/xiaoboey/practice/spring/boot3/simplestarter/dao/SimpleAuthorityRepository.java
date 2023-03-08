package top.xiaoboey.practice.spring.boot3.simplestarter.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.xiaoboey.practice.spring.boot3.simplestarter.entity.SimpleAuthority;

/**
 * @author xiaoqb
 */
public interface SimpleAuthorityRepository extends JpaRepository<SimpleAuthority, Long> {

}
