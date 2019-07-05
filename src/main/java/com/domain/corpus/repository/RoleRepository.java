package com.domain.corpus.repository;

import com.domain.corpus.model.role.Role;
import com.domain.corpus.model.role.RoleName;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
