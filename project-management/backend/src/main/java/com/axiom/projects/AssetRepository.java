package com.axiom.projects;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {
  List<Asset> findByProjectId(Long id);
}
