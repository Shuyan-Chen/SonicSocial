package com.shuyan.sonicsocial.repository;

import com.shuyan.annotation.AutoFill;
import com.shuyan.entity.User;
import com.shuyan.sonicsocial.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User save(User user);

    boolean findByUsername(String username);

    boolean findByEmail(String email);

    User getUserByUsername(String username);

}
