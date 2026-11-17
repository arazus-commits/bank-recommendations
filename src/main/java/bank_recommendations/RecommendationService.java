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

    public RecommendationService(
            List<RecommendationRuleSet> staticRules,
            RuleRepository ruleRepository,
            DynamicRuleEngine dynamicRuleEngine) {
        this.staticRules = staticRules;
        this.ruleRepository = ruleRepository;
        this.dynamicRuleEngine = dynamicRuleEngine;
    }

    public List<RecommendationDto> getRecommendations(UUID userId) {
        List<RecommendationDto> recommendations = new ArrayList<>();

        for (RecommendationRuleSet rule : staticRules) {
            rule.check(userId).ifPresent(recommendations::add);
        }

        List<RuleEntity> dynamicRules = ruleRepository.findAll();
        for (RuleEntity rule : dynamicRules) {
            if (dynamicRuleEngine.evaluate(rule.getRule(), userId)) {
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