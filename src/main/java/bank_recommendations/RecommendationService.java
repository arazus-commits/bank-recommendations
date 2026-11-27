package bank_recommendations;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> staticRules;
    private final RuleRepository ruleRepository;
    private final DynamicRuleEngine dynamicRuleEngine;
    private final RuleStatisticsRepository ruleStatisticsRepository; // Исправлено на RuleStatisticsRepository

    public RecommendationService(
            List<RecommendationRuleSet> staticRules,
            RuleRepository ruleRepository,
            DynamicRuleEngine dynamicRuleEngine,
            RuleStatisticsRepository ruleStatisticsRepository) { // Исправлено на RuleStatisticsRepository
        this.staticRules = staticRules;
        this.ruleRepository = ruleRepository;
        this.dynamicRuleEngine = dynamicRuleEngine;
        this.ruleStatisticsRepository = ruleStatisticsRepository;
    }

    public List<RecommendationDto> getRecommendations(UUID userId) {
        List<RecommendationDto> recommendations = new ArrayList<>();

        // 1. Проверяем старые фиксированные правила
        for (RecommendationRuleSet rule : staticRules) {
            rule.check(userId).ifPresent(recommendations::add);
        }

        // 2. Проверяем динамические правила из базы данных
        List<RuleEntity> dynamicRules = ruleRepository.findAll();
        for (RuleEntity rule : dynamicRules) {
            // Обрати внимание: передаем сюда rule (сам объект), а не rule.getRule()
            if (dynamicRuleEngine.evaluate(rule, userId)) {
                recommendations.add(new RecommendationDto(
                        rule.getProductName(),
                        rule.getProductId(),
                        rule.getProductText()
                ));

            }
        }

        return recommendations;
    }
}
