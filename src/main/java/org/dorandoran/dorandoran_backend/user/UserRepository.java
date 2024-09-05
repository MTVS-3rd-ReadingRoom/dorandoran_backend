package org.dorandoran.dorandoran_backend.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserInfo, Long> {
    @Query("SELECT u FROM UserInfo u WHERE u.id = :userId")
    UserInfo findByUserId(String userId);
}
