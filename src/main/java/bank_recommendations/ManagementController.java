package bank_recommendations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/management")
public class ManagementController {

    private final BuildProperties buildProperties;
    private final RecommendationsRepository recommendationsRepository;

    // BuildProperties может быть null, если проект собран не через Maven (например, просто нажатием Run в IDEA)
    @Autowired(required = false)
    public ManagementController(BuildProperties buildProperties, RecommendationsRepository recommendationsRepository) {
        this.buildProperties = buildProperties;
        this.recommendationsRepository = recommendationsRepository;
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getInfo() {
        Map<String, String> info = new HashMap<>();

        if (buildProperties != null) {
            info.put("name", buildProperties.getArtifact());
            info.put("version", buildProperties.getVersion());
        } else {
            // Заглушка, если BuildProperties не сгенерировался (запуск через IDEA)
            info.put("name", "bank-recommendations");
            info.put("version", "unknown");
        }

        return ResponseEntity.ok(info);
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        // Вызываем метод, который Максим добавит в Repository (Issue #7)
        recommendationsRepository.clearCaches();
        return ResponseEntity.ok().build();
    }
}