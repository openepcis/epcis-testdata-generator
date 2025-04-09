package io.openepcis.testdata.generator.format;

import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.template.RandomGenerators;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Setter
@Getter
@RegisterForReflection
public class ValueTypeSyntax implements Serializable {
  @NotNull(message = "Type cannot be Null for Static/Random value generation")
  @Schema(
      type = SchemaType.STRING,
      description = "Type of the Static/Random value generation static/random.")
  private String type;

  @Schema(type = SchemaType.NUMBER, description = "Static value if the type is static.")
  private double staticValue;

  @Schema(
      type = SchemaType.INTEGER,
      description = "randomID associated to randomGenerators if the type is random.")
  private int randomID;

  // Method to get the value, either static or generated randomly
  public double getValue(final List<RandomGenerators> randomGenerators) {
    if (this.type.equals("static")) {
      return this.staticValue;
    } else if (this.type.equals("random") && randomGenerators != null) {
      return generateRandomValue(randomGenerators);
    }

    throw new TestDataGeneratorException("Unknown type for Value generation : " + this.type);
  }

  private double generateRandomValue(final List<RandomGenerators> randomGenerators) {
    // Find the corresponding RandomGenerators entry
    final RandomGenerators generatorConfig =
        randomGenerators.stream()
            .filter(r -> r.getRandomID() == this.randomID)
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("No generator found for randomID: " + randomID));

    double sample =
        generatorConfig
            .getTriangularDistribution()
            .sample(); // Generate and return the random value

    // If user specified format for RandomGenerator then format based on specific format
    if (!StringUtils.isBlank(generatorConfig.getFormatValue())) {
      try {
        return Double.parseDouble(
            String.format(Locale.US, generatorConfig.getFormatValue(), sample));
      } catch (Exception ex) {
        throw new TestDataGeneratorException(
            "Invalid format value provided for formatting Random value : " + ex.getMessage(), ex);
      }
    }

    return Double.parseDouble(String.format("%.3f", sample));
  }
}
