package io.openepcis.testdata.generator.sensors;

import io.openepcis.testdata.generator.format.ValueTypeSyntax;
import io.openepcis.testdata.generator.template.RandomGenerators;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

@ToString(callSuper = true)
@Getter
@Setter
@RegisterForReflection
public class SensorReportType {
    private Boolean booleanValue;

    private String bizRules;

    private URI chemicalSubstance;

    private String component;

    private String coordinateReferenceSystem;

    private URI dataProcessingMethod;

    private URI deviceID;

    private URI deviceMetadata;

    private String exception;

    private String hexBinaryValue;

    private ValueTypeSyntax maxValue;

    private ValueTypeSyntax meanValue;

    private URI microorganism;

    private ValueTypeSyntax minValue;

    private ValueTypeSyntax percRank;

    private ValueTypeSyntax percValue;

    private URI rawData;

    private ValueTypeSyntax sDev;

    private String stringValue;

    private OffsetDateTime time;

    private URI type;

    private String uom;

    private URI uriValue;

    private ValueTypeSyntax value;

    private List<RandomGenerators> randomGenerators;

    public double getMeanValue() {
        return this.meanValue.getValue(this.randomGenerators);
    }

    public double getMinValue() {
        return this.minValue.getValue(this.randomGenerators);
    }

    public double getValue() {
        return this.value.getValue(this.randomGenerators);
    }
}
