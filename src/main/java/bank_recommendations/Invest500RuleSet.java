package bank_recommendations;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class Invest500RuleSet implements RecommendationRuleSet {

    private final RecommendationsRepository repository;

    public Invest500RuleSet(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasDebit = repository.hasProductType(userId, "DEBIT");
        boolean hasInvest = repository.hasProductType(userId, "INVEST");
        double savingSum = repository.getSumTransactionsAmount(userId, "SAVING");

        if (hasDebit && hasInvest && savingSum > 1000) {
            return Optional.of(new RecommendationDto("Invest 500", UUID.randomUUID(), "Рекомендация Invest 500"));
        }
        return Optional.empty();
    }
}