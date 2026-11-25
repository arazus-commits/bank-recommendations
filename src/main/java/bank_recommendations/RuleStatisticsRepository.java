package bank_recommendations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RuleStatisticsRepository extends JpaRepository<RuleStatistics, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE rule_statistics SET hit_count = hit_count + 1 WHERE rule_id = :ruleId",
            nativeQuery = true)
    void incrementHitCount(@Param("ruleId") Long ruleId);

    @Query("SELECT rs FROM RuleStatistics rs LEFT JOIN FETCH rs.rule")
    List<RuleStatistics> findAllWithRules();
}