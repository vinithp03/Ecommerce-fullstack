package com.vinith.catalog.RepositoryLayer;

import com.vinith.catalog.EntityLayer.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    
}