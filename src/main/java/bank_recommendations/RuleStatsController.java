package bank_recommendations;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rule")
public class RuleStatsController {

    private final RuleStatisticsRepository statisticsRepository;

    public RuleStatsController(RuleStatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<RuleStatsResponse> getStats() {
        List<RuleStatistics> stats = statisticsRepository.findAllWithRules();
        
        List<RuleStatsItem> items = stats.stream()
                .map(s -> new RuleStatsItem(s.getRuleId(), s.getCount()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new RuleStatsResponse(items));
    }

    public static class RuleStatsResponse {
        private List<RuleStatsItem> stats;

        public RuleStatsResponse(List<RuleStatsItem> stats) {
            this.stats = stats;
        }

        public List<RuleStatsItem> getStats() {
            return stats;
        }
    }

    public static class RuleStatsItem {
        private Long rule_id;
        private Long count;

        public RuleStatsItem(Long rule_id, Long count) {
            this.rule_id = rule_id;
            this.count = count;
        }

        public Long getRule_id() {
            return rule_id;
        }

        public Long getCount() {
            return count;
        }
    }
}