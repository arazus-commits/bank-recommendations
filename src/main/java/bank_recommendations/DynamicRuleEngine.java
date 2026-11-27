package bank_recommendations;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

/**
 * Движок вычисления динамических правил рекомендаций.
 * <p>
 * Проверяет набор условий ({@link RuleQuery}), заданных для динамического правила {@link RuleEntity},
 * применительно к транзакциям конкретного пользователя. При выполнении всех условий увеличивает
 * счётчик срабатываний правила через {@link RuleService}.
 */
@Service
public class DynamicRuleEngine {

    private final RecommendationsRepository repository;
    private final RuleService ruleService;

    /**
     * Создаёт экземпляр движка динамических правил.
     *
     * @param repository  репозиторий для получения агрегированных данных о транзакциях пользователя
     * @param ruleService сервис управления правилами и обновления статистики срабатываний
     */
    public DynamicRuleEngine(RecommendationsRepository repository, RuleService ruleService) {
        this.repository = repository;
        this.ruleService = ruleService;
    }

    /**
     * Проверяет, соответствует ли пользователь всем условиям заданного динамического правила.
     * <p>
     * Если список условий пуст или равен {@code null}, возвращает {@code false}.
     * При успешном совпадении всех условий инкрементирует счётчик срабатываний правила.
     *
     * @param rule   динамическое правило с набором запросов {@link RuleQuery}
     * @param userId идентификатор пользователя
     * @return {@code true}, если выполнены все условия правила; иначе {@code false}
     */
    public boolean evaluate(RuleEntity rule, UUID userId) {
        List<RuleQuery> queries = rule.getRule();
        if (queries == null || queries.isEmpty()) {
            return false;
        }

        boolean matched = queries.stream().allMatch(q -> evaluateQuery(q, userId));

        if (matched) {
            ruleService.incrementHitCount(rule.getId());
        }
        return matched;
    }

    /**
     * Вычисляет одиночное условие {@link RuleQuery} для пользователя с учётом флага отрицания {@code negate}.
     *
     * @param q      условие правила (тип запроса, аргументы, флаг отрицания)
     * @param userId идентификатор пользователя
     * @return результат проверки условия с учётом {@link RuleQuery#isNegate()}
     */
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

    /**
     * Сравнивает сумму транзакций пользователя по заданному типу продукта и типу операции с пороговым значением.
     *
     * @param userId          идентификатор пользователя
     * @param productType     тип банковского продукта (например, {@code DEBIT}, {@code SAVING})
     * @param transactionType тип транзакции ({@code DEPOSIT} или {@code WITHDRAW})
     * @param operator        оператор сравнения ({@code >}, {@code <}, {@code =}, {@code >=}, {@code <=})
     * @param threshold       пороговая сумма для сравнения
     * @return {@code true}, если условие сравнения выполняется; {@code false} иначе или при неизвестном операторе
     */
    public boolean compareTransactionSum(UUID userId, String productType, String transactionType, String operator, int threshold) {
        double sum = repository.getSumTransactionsAmount(userId, productType, transactionType);
        return switch (operator) {
            case ">" -> sum > threshold;
            case "<" -> sum < threshold;
            case "=" -> sum == threshold;
            case ">=" -> sum >= threshold;
            case "<=" -> sum <= threshold;
            default -> false;
        };
    }

    /**
     * Сравнивает общую сумму пополнений ({@code DEPOSIT}) и списаний ({@code WITHDRAW}) пользователя по типу продукта.
     *
     * @param userId      идентификатор пользователя
     * @param productType тип банковского продукта
     * @param operator    оператор сравнения ({@code >}, {@code <}, {@code =}, {@code >=}, {@code <=})
     * @return {@code true}, если соотношение сумм пополнений и списаний удовлетворяет оператору; иначе {@code false}
     */
    public boolean compareDepositWithdraw(UUID userId, String productType, String operator) {
        double deposit = repository.getSumTransactionsAmount(userId, productType, "DEPOSIT");
        double withdraw = repository.getSumTransactionsAmount(userId, productType, "WITHDRAW");
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