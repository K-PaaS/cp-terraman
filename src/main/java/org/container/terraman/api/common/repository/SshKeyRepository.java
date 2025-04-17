package org.container.terraman.api.common.repository;

import org.container.terraman.api.common.model.SshKeyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface SshKeyRepository extends JpaRepository<SshKeyModel, Long> {
    SshKeyModel findByName(@Param("name") String name) throws Exception;
}
