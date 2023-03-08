package top.xiaoboey.practice.spring.boot3.simplestarter.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.xiaoboey.practice.spring.boot3.simplestarter.entity.SimpleLog;

/**
 * @author xiaoqb
 */
public interface SimpleLogRepository extends JpaRepository<SimpleLog, Long> {

}
