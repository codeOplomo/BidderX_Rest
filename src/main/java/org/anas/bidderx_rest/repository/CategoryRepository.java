package org.anas.bidderx_rest.repository;

import org.anas.bidderx_rest.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
