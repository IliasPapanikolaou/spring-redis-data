package com.unipi.ipap.springdatarediscrud.controller;

import com.unipi.ipap.springdatarediscrud.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    // http://localhost:8080/api/recommendations/user/-1362741538652509330
    @GetMapping("/user/{userId}")
    public Set<String> userRecommendations(@PathVariable("userId") String userId) {
        return recommendationService.getBookRecommendationsFromCommonPurchasesForUser(userId);
    }

    // http://localhost:8080/api/recommendations/isbn/1782161635/pairings
    @GetMapping("/isbn/{isbn}/pairings")
    public Set<String> frequentPairings(@PathVariable("isbn") String isbn) {
        return recommendationService.getFrequentlyBoughtTogether(isbn);
    }
}
