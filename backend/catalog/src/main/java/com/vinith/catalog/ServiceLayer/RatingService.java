package com.vinith.catalog.ServiceLayer;

import com.vinith.catalog.EntityLayer.Rating;
import com.vinith.catalog.RepositoryLayer.RatingRepository;
import org.springframework.stereotype.Service;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Rating saveRating(Rating rating) {
        return ratingRepository.save(rating);
    }
}