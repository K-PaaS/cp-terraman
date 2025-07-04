package org.container.terraman.api.common.repository;

import org.container.terraman.api.common.model.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface AccountRepository extends JpaRepository<AccountModel, Long> {
    AccountModel findById(@Param("id") int id) throws Exception;
}
