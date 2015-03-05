package org.watson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.deltaspike.core.api.config.ConfigProperty;

/**
 * CDI Bean that reads VCAP_SERVICES, a json configuration in system variables
 * that describes services in CloudFoundry. Properties are set by BlueMix or you
 * manually e.g. to src/main/resources/META-INF/apache-deltaspike.properties
 * during local development.
 *
 * @author Matti Tahvonen
 */
public class BluemixServices {

    @Inject
    @ConfigProperty(name = "VCAP_SERVICES")
    private String configJson;
    private JsonNode jsonTree;
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        try {
            jsonTree = objectMapper.readTree(configJson);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    public GenericServiceConfig getQuestionAndAnswerConfig() {
        return getGenericServiceConfigs("question_and_answer").get(0);
    }

    public GenericServiceConfig getTextToSpeechConfig() {
        return getGenericServiceConfigs("text_to_speech").get(0);
    }

    public GenericServiceConfig getVisualRecognitionConfig() {
        return getGenericServiceConfigs("visual_recognition").get(0);
    }

    private List<GenericServiceConfig> getGenericServiceConfigs(String key) {
        JsonNode node = jsonTree.get(key);
        JavaType type = objectMapper.getTypeFactory().
                constructCollectionType(List.class,
                        GenericServiceConfig.class);
        return objectMapper.convertValue(node, type);
    }

}
