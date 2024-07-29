package io.openepcis.testdata.generator.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.openepcis.testdata.generator.format.RandomValueGenerator;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.random.RandomGenerator;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;

@Setter
@Getter
@RegisterForReflection
public class RandomGenerators implements Serializable {
    @Schema(
            type = SchemaType.NUMBER,
            description = "ID associated with the random generation type.")
    private int randomID;

    @Schema(
            type = SchemaType.STRING,
            description = "Name associated with the random generation type.")
    private String name;

    @Schema(
            type = SchemaType.STRING,
            description = "Description associated with the random generation type.")
    private String description;

    @Schema(
            type = SchemaType.NUMBER,
            description = "Minimum value associated with the random value generation.")
    private double minValue;

    @Schema(
            type = SchemaType.NUMBER,
            description = "Maximum value associated with the random value generation.")
    private double maxValue;

    @Schema(
            type = SchemaType.NUMBER,
            description = "Mean value associated with the random value generation.")
    private double meanValue;

    @Schema(
            type = SchemaType.NUMBER,
            description = "Seed value associated with the random value generation for Mersenne Twister.")
    private long seedValue;

    @Schema(
            type = SchemaType.STRING,
            description = "Format value associated with the random value generation.")
    private String formatValue;

    @Schema(
            type = SchemaType.NUMBER,
            description = "Distribution type associated with the random value generation.")
    private String distributionType;

    @JsonIgnore
    private RandomGenerator randomGenerator;
}
