package top.xiaoboey.practice.single.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.xiaoboey.practice.single.entity.SimpleAuthority;

/**
 * @author xiaoqb
 */
public interface SimpleAuthorityRepository extends JpaRepository<SimpleAuthority, Long> {

}
