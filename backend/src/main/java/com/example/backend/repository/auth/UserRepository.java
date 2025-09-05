package com.example.backend.repository.auth;

import com.example.backend.entity.auth.Role;
import com.example.backend.entity.auth.RoleCode;
import com.example.backend.entity.auth.User;
import com.example.backend.entity.auth.UserStatus;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    List<User> findAllByRoles_CodeAndStatus(RoleCode code, UserStatus status);

    @Modifying
    @Query("DELETE FROM OtpCode o WHERE o.user.id = :userId")
    void deleteOtpCodesForUser(@Param("userId") Long userId);

    Optional<User> findByEmailIgnoreCase(String email);

    @Query("SELECT r.id FROM User u JOIN u.roles r WHERE u.id = :userId")
    List<Long> getRoleIdsByUserId(@Param("userId") Long userId);

}
