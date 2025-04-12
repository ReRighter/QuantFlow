package edu.zhou.quantflow.repository;


import edu.zhou.quantflow.entity.Users;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


/**
 * @Author Righter
 * @Description
 * @Date since 4/12/2025
 */
public interface UserRepository extends ReactiveCrudRepository<Users,Integer> {
    Mono<Users> findByUsername(String username);
}
