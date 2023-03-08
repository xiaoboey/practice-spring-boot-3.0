package top.xiaoboey.practice.spring.boot3.simplestarter.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import top.xiaoboey.practice.spring.boot3.simplestarter.config.CaffeineConfiguration;
import top.xiaoboey.practice.spring.boot3.simplestarter.dao.SimpleUserRepository;

/**
 * @author xiaoqb
 */
public class SimpleUserService implements UserDetailsService {
    private final SimpleUserRepository simpleUserRepository;

    public SimpleUserService(SimpleUserRepository simpleUserRepository) {
        this.simpleUserRepository = simpleUserRepository;
    }

    @Cacheable(cacheNames = CaffeineConfiguration.CACHE_SIMPLE_USER, key = "#username")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return simpleUserRepository.findByUsername(username);
    }
}
