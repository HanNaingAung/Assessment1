package org.example.repository;

import org.example.model.Promotion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends MongoRepository<Promotion,Long> {
    Optional<Promotion> findByItemId(Long itemId);
}
