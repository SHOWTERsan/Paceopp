package ru.santurov.paceopp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.santurov.paceopp.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUuid(String uuid);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.tokens t WHERE u.uuid = :uuid")
    Optional<User> findByUuidAndFetchTokensEagerly(@Param("uuid") String uuid);

    Optional<User> findByEmail(String email);

}
