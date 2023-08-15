package com.powerbike.repositories;

import com.powerbike.models.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity,Long> {

    Optional<UserEntity> findByEmail(String username);
    @Query("select u from UserEntity u where u.email = ?1")
    Optional<UserEntity> getName(String username);
}

