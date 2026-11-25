package bank_recommendations;

import jakarta.persistence.*;

@Entity
@Table(name = "rule_statistics")
public class RuleStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_id", nullable = false, unique = true)
    private Long ruleId;


    @Column(name = "hit_count", nullable = false)
    private Long count = 0L;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    private RuleEntity rule;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRuleId() { return ruleId; }
    public void setRuleId(Long ruleId) { this.ruleId = ruleId; }

    public Long getCount() { return count; }          // было: public void getCount()
    public void setCount(Long count) { this.count = count; }

    public RuleEntity getRule() { return rule; }      // было: public void RuleEntity getRule()
    public void setRule(RuleEntity rule) { this.rule = rule; }
}