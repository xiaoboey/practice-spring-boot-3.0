package top.xiaoboey.practice.single.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.xiaoboey.practice.single.entity.SimpleLog;

/**
 * @author xiaoqb
 */
public interface SimpleLogRepository extends JpaRepository<SimpleLog, Long> {

}
