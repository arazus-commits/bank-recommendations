package bank_recommendations;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.UUID;

@Repository
public class RecommendationsRepository {

    private final JdbcTemplate jdbcTemplate;

    private record CountKey(UUID userId,String productType){}
    private record SumKey(UUID userId,String productType,String transactionType){}

    private final Cache<CountKey,Integer> countCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build();

    private final Cache<SumKey,Double> sumCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build();

    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getTransactionCount(UUID userId, String productType) {
        CountKey key = new CountKey(userId, productType);
        Integer count = countCache.get(key, k -> {
            String sql = """
                    SELECT COUNT(*) FROM transactions t
                    JOIN products p ON t.product_id = p.id
                    WHERE t.user_id = ? AND p.type = ?""";
            Integer result = jdbcTemplate.queryForObject(sql, Integer.class, k.userId(), k.productType());
            return result != null ? result : 0;
        });
        return count != null ? count : 0;
    }

    public boolean hasProductType(UUID userId, String productType) {
        return getTransactionCount(userId, productType) > 0;
        }

    public double getSumTransactionsAmount(UUID userId, String productType, String transactionType) {
        String sql = "SELECT COALESCE(SUM(t.amount), 0) FROM transactions t JOIN products p ON t.product_id = p.id WHERE t.user_id = ? AND p.type = ? AND t.type = ?";
        Double sum = jdbcTemplate.queryForObject(sql, Double.class, userId, productType, transactionType);
        return sum != null ? sum : 0.0;
    }
}
