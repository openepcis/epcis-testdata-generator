package io.openepcis.testdata.generator.format;

import io.openepcis.testdata.generator.template.RandomGenerators;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@RegisterForReflection
public class ValueTypeSyntax implements Serializable {
    @NotNull(message = "Type cannot be Null for Static/Random value generation")
    @Schema(type = SchemaType.STRING, description = "Type of the Static/Random value generation static/random.")
    private String type;

    @Schema(type = SchemaType.NUMBER, description = "Static value if the type is static.")
    private double staticValue;

    @Schema(type = SchemaType.INTEGER, description = "randomID associated to randomGenerators if the type is random.")
    private int randomID;

    // Method to get the value, either static or generated randomly
    public double getValue(final List<RandomGenerators> randomGenerators) {
        if (this.type.equals("static")) {
            return this.staticValue;
        } else if (this.type.equals("random")) {
            return generateRandomValue(randomGenerators);
        }
        throw new IllegalStateException("Unknown type for Value generation : " + this.type);
    }

    private double generateRandomValue(final List<RandomGenerators> randomGenerators) {
        // Find the corresponding RandomGenerators entry
        final RandomGenerators generatorConfig = randomGenerators.stream()
                .filter(r -> r.getRandomID() == this.randomID)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No generator found for randomID: " + randomID));

        // Instantiate RandomValueGenerator with the found config
        final RandomValueGenerator generator = new RandomValueGenerator(generatorConfig);
        return generator.sample(); // Generate and return the random value
    }
}
