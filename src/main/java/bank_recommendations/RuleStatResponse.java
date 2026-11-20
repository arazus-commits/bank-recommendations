package bank_recommendations;

public class RuleStatResponse {
    private Long rule_id;
    private int count;

    public RuleStatResponse(Long rule_id, int count) {
        this.rule_id = rule_id;
        this.count = count;
    }

    public Long getRule_id() { return rule_id; }
    public int getCount() { return count; }
}