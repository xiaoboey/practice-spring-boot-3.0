package top.xiaoboey.practice.single;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.xiaoboey.practice.single.dao.SimpleAuthorityRepository;
import top.xiaoboey.practice.single.dao.SimpleUserRepository;
import top.xiaoboey.practice.single.entity.SimpleAuthority;
import top.xiaoboey.practice.single.entity.SimpleUser;

import java.util.Arrays;

/**
 * @author xiaoqb
 */
@SpringBootApplication
public class SingleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SingleApplication.class, args);
	}

	@Autowired
	private SimpleAuthorityRepository simpleAuthorityRepository;
	@Autowired
	private SimpleUserRepository simpleUserRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostConstruct
	public void initialize() {
		final long adminRoleId = 1L;
		final long workerRoleId = 2L;
		final long diggingAuthorityId = 200L;

		//Initialize the simple_authority table
		if (!simpleAuthorityRepository.findById(adminRoleId).isPresent()) {
			simpleAuthorityRepository.save(new SimpleAuthority(adminRoleId, "ROLE_ADMIN"));
			simpleAuthorityRepository.save(new SimpleAuthority(workerRoleId, "ROLE_WORKER"));
			simpleAuthorityRepository.save(new SimpleAuthority(diggingAuthorityId, "DIGGING"));
		}

		//Initialize the simple_user table
		SimpleUser admin = simpleUserRepository.findByUsername("admin");
		if (admin == null) {
			//Add user admin and associate role ROLE_ADMIN
			admin = new SimpleUser(adminRoleId, "admin", passwordEncoder.encode("admin"));
			admin.setAuthorities(simpleAuthorityRepository.findAllById(Arrays.asList(adminRoleId)));
			simpleUserRepository.save(admin);

			//Add user worker and associate ROLE_WORKER and DIGGING
			SimpleUser worker = new SimpleUser(workerRoleId, "worker", passwordEncoder.encode("worker"));
			worker.setAuthorities(simpleAuthorityRepository.findAllById(Arrays.asList(workerRoleId, diggingAuthorityId)));
			simpleUserRepository.save(worker);
		}
	}
}
