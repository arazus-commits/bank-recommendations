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

    // Делаем поле необязательным, чтобы приложение запускалось даже без сгенерированной версии
    @Autowired(required = false)
    private BuildProperties buildProperties;

    private final RecommendationsRepository recommendationsRepository;

    public ManagementController(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getInfo() {
        Map<String, String> info = new HashMap<>();

        if (buildProperties != null) {
            info.put("name", buildProperties.getArtifact());
            info.put("version", buildProperties.getVersion());
        } else {
            info.put("name", "bank-recommendations");
            info.put("version", "1.0.0");
        }

        return ResponseEntity.ok(info);
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        recommendationsRepository.clearCaches();
        return ResponseEntity.ok().build();
    }
}