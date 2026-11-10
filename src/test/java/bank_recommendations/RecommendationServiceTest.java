package bank_recommendations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Современный способ инициализации моков
class RecommendationServiceTest {

    @Mock
    private RecommendationRuleSet rule1;

    @Mock
    private RecommendationRuleSet rule2;

    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        // openMocks больше не нужен, так как есть @ExtendWith
        recommendationService = new RecommendationService(Arrays.asList(rule1, rule2));
    }

    @Test
    void testGetRecommendationsSuccess() {
        UUID userId = UUID.randomUUID();
        
        RecommendationDto rec1 = new RecommendationDto("Invest 500", UUID.randomUUID(), "Описание");
        RecommendationDto rec2 = new RecommendationDto("Top Saving", UUID.randomUUID(), "Описание");

        when(rule1.check(userId)).thenReturn(Optional.of(rec1));
        when(rule2.check(userId)).thenReturn(Optional.of(rec2));

        List<RecommendationDto> result = recommendationService.getRecommendations(userId);

        assertEquals(2, result.size());
        assertEquals("Invest 500", result.get(0).getName());
    }

    @Test
    void testGetRecommendationsEmpty() {
        UUID userId = UUID.randomUUID();

        when(rule1.check(userId)).thenReturn(Optional.empty());
        when(rule2.check(userId)).thenReturn(Optional.empty());

        List<RecommendationDto> result = recommendationService.getRecommendations(userId);

        assertEquals(0, result.size());
    }
}