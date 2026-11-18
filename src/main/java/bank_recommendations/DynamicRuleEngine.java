package bank_recommendations;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class DynamicRuleEngine {

    private final RecommendationsRepository repository;

    public DynamicRuleEngine(RecommendationsRepository repository) {
        this.repository = repository;
    }

    public boolean evaluate(List<RuleQuery> queries, UUID userId) {
        if (queries == null || queries.isEmpty()) {
            return false;
        }
        return queries.stream().allMatch(q -> evaluateQuery(q, userId));
    }

    private boolean evaluateQuery(RuleQuery q, UUID userId) {
        boolean result = false;
        List<String> args = q.getArguments();

        switch (q.getQuery()) {
            case "USER_OF":
                result = repository.hasProductType(userId, args.get(0));
                break;
            case "ACTIVE_USER_OF":
                result = repository.getTransactionCount(userId, args.get(0)) >= 5;
                break;
            case "TRANSACTION_SUM_COMPARE":
                result = repository.compareTransactionSum(userId, args.get(0), args.get(1), args.get(2), Integer.parseInt(args.get(3)));
                break;
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW":
                result = repository.compareDepositWithdraw(userId, args.get(0), args.get(1));
                break;
        }

        return q.isNegate() ? !result : result;
    }
    public boolean compareTransactionSum(UUID userId, String productType, String transactionType, String operator, int threshold) {
        double sum = getSumTransactionsAmount(userId, productType, transactionType);
        return switch (operator) {
            case ">" -> sum > threshold;
            case "<" -> sum < threshold;
            case "=" -> sum == threshold;
            case ">=" -> sum >= threshold;
            case "<=" -> sum <= threshold;
            default -> false;
        };
    }
    public boolean compareDepositWithdraw(UUID userId, String productType, String operator) {
        double deposit = getSumTransactionsAmount(userId, productType, "DEPOSIT");
        double withdraw = getSumTransactionsAmount(userId, productType, "WITHDRAW");
        return switch (operator) {
            case ">" -> deposit > withdraw;
            case "<" -> deposit < withdraw;
            case "=" -> deposit == withdraw;
            case ">=" -> deposit >= withdraw;
            case "<=" -> deposit <= withdraw;
            default -> false;
        };
    }
}