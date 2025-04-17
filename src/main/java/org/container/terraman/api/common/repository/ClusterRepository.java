package org.container.terraman.api.common.repository;

import org.container.terraman.api.common.model.ClusterModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface ClusterRepository extends JpaRepository<ClusterModel, String> {
}
