package bank_recommendations;

import java.util.List;

public class RuleStatsListResponse {
    private List<RuleStatResponse> stats;

    public RuleStatsListResponse(List<RuleStatResponse> stats) {
        this.stats = stats;
    }

    public List<RuleStatResponse> getStats() { return stats; }
}
