package com.sliit.aams.servicepackage.repository;

import com.sliit.aams.servicepackage.model.serviceItems;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceRepository extends JpaRepository<serviceItems, Long> {
    List<serviceItems> findByCategoryIgnoreCase(String category);
}