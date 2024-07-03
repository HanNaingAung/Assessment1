package org.example.repository;

import org.example.model.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends MongoRepository<Promotion,Long> {
    Page<Promotion> findAll(Pageable pageable);

    Optional<Promotion> findByItemId(Long itemId);
}
