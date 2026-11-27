package bank_recommendations;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер для получения персональных рекомендаций банковских продуктов.
 * <p>
 * Обрабатывает запросы по пути {@code /recommendation}.
 */
@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Создаёт контроллер рекомендаций.
     *
     * @param recommendationService сервис подбора рекомендаций на основе статических и динамических правил
     */
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Возвращает список рекомендованных банковских продуктов для указанного пользователя.
     *
     * @param userId уникальный идентификатор пользователя (UUID)
     * @return объект {@link RecommendationResponse}, содержащий идентификатор пользователя и список рекомендаций
     */
    @GetMapping("/{user_id}")
    public RecommendationResponse getRecommendations(@PathVariable("user_id") UUID userId) {
        List<RecommendationDto> recs = recommendationService.getRecommendations(userId);
        return new RecommendationResponse(userId, recs);
    }
}