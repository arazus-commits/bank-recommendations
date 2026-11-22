package bank_recommendations;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;
    private final RuleStatisticsRepository statisticsRepository;

    public RuleService(RuleRepository ruleRepository,
                       RuleStatisticsRepository statisticsRepository) {
        this.ruleRepository = ruleRepository;
        this.statisticsRepository = statisticsRepository;
    }

    @Transactional
    public RuleEntity createRule(RuleEntity ruleEntity) {
        RuleEntity saved = ruleRepository.save(ruleEntity);

        RuleStatistics statistics = new RuleStatistics();
        statistics.setRuleId(saved.getId());
        statistics.setCount(0L);
        statisticsRepository.save(statistics);

        return saved;
    }

    public List<RuleEntity> getAllRules() {
        return ruleRepository.findAll();
    }

    @Transactional
    public void deleteRule(Long id) {
        ruleRepository.deleteById(id);
    }

    @Transactional
    public void incrementHitCount(Long ruleId) {
        statisticsRepository.incrementHitCount(ruleId);
    }
}