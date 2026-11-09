package bank_recommendations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class RecommendationServiceTest {

    @Mock
    private RecommendationRuleSet rule1;

    @Mock
    private RecommendationRuleSet rule2;

    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recommendationService = new RecommendationService(Arrays.asList(rule1, rule2));
    }

    @Test
    void testGetRecommendationsSuccess() {
        UUID userId = UUID.randomUUID();
        
        RecommendationDto rec1 = new RecommendationDto("Invest 500", UUID.randomUUID(), "Описание инвестиции");
        RecommendationDto rec2 = new RecommendationDto("Top Saving", UUID.randomUUID(), "Описание накопления");

        // Настраиваем поведение моков
        when(rule1.check(userId)).thenReturn(Optional.of(rec1));
        when(rule2.check(userId)).thenReturn(Optional.of(rec2));

        List<RecommendationDto> result = recommendationService.getRecommendations(userId);

        assertEquals(2, result.size());
        assertEquals("Invest 500", result.get(0).getName());
        assertEquals("Top Saving", result.get(1).getName());
    }

    @Test
    void testGetRecommendationsEmpty() {
        UUID userId = UUID.randomUUID();

        when(rule1.check(userId)).thenReturn(Optional.empty());
        when(rule2.check(userId)).thenReturn(Optional.empty());

        List<RecommendationDto> result = recommendationService.getRecommendations(userId);

        assertEquals(0, result.size());
    }
    
    @Test
    void testGetRecommendationsPartial() {
        UUID userId = UUID.randomUUID();
        RecommendationDto rec1 = new RecommendationDto("Credit", UUID.randomUUID(), "Простой кредит");

        when(rule1.check(userId)).thenReturn(Optional.of(rec1));
        when(rule2.check(userId)).thenReturn(Optional.empty());

        List<RecommendationDto> result = recommendationService.getRecommendations(userId);

        assertEquals(1, result.size());
        assertEquals("Credit", result.get(0).getName());
    }
}