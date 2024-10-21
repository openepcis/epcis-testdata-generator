package io.openepcis.testdata.generator.sensors;

import io.openepcis.model.epcis.SensorReport;
import io.openepcis.testdata.generator.format.ValueTypeSyntax;
import io.openepcis.testdata.generator.template.RandomGenerators;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.ObjDoubleConsumer;

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

    public SensorReport format(final List<RandomGenerators> randomGenerators) {
        final SensorReport sensorReport = new SensorReport();


        // Invoke setDoubleValue for each ValueTypeSyntax field and set their double value if not null
        setDoubleValue(sensorReport, SensorReport::setMaxValue, this.maxValue, randomGenerators);
        setDoubleValue(sensorReport, SensorReport::setMinValue, this.minValue, randomGenerators);
        setDoubleValue(sensorReport, SensorReport::setMeanValue, this.meanValue, randomGenerators);
        setDoubleValue(sensorReport, SensorReport::setPercRank, this.percRank, randomGenerators);
        setDoubleValue(sensorReport, SensorReport::setPercValue, this.percValue, randomGenerators);
        setDoubleValue(sensorReport, SensorReport::setSDev, this.sDev, randomGenerators);
        setDoubleValue(sensorReport, SensorReport::setValue, this.value, randomGenerators);

        // Invoke setURIValue for each URI field and set their value if not null
        setUriValue(sensorReport, SensorReport::setChemicalSubstance, this.chemicalSubstance);
        setUriValue(sensorReport, SensorReport::setDataProcessingMethod, this.dataProcessingMethod);
        setUriValue(sensorReport, SensorReport::setDeviceID, this.deviceID);
        setUriValue(sensorReport, SensorReport::setDeviceMetadata, this.deviceMetadata);
        setUriValue(sensorReport, SensorReport::setMicroorganism, this.microorganism);
        setUriValue(sensorReport, SensorReport::setRawData, this.rawData);
        setUriValue(sensorReport, SensorReport::setType, this.type);
        setUriValue(sensorReport, SensorReport::setUriValue, this.uriValue);

        // Invoke setStringValue for each String field and set their value if not null
        setStringValue(sensorReport, SensorReport::setBizRules, this.bizRules);
        setStringValue(sensorReport, SensorReport::setComponent, this.component);
        setStringValue(sensorReport, SensorReport::setCoordinateReferenceSystem, this.coordinateReferenceSystem);
        setStringValue(sensorReport, SensorReport::setException, this.exception);
        setStringValue(sensorReport, SensorReport::setHexBinaryValue, this.hexBinaryValue);
        setStringValue(sensorReport, SensorReport::setStringValue, this.stringValue);
        setStringValue(sensorReport, SensorReport::setUom, this.uom);

        // Invoke other values if not null/ value provided
        sensorReport.setBooleanValue(this.booleanValue != null ? this.booleanValue : null);
        sensorReport.setTime(this.time != null ? this.time : null);

        return sensorReport;
    }

    //Method to check for null and assign the values for ValueTypeSyntax
    private void setDoubleValue(final SensorReport sensorReport, final ObjDoubleConsumer<SensorReport> setter, final ValueTypeSyntax valueType, final List<RandomGenerators> randomGenerators) {
        if (valueType != null) {
            setter.accept(sensorReport, valueType.getValue(randomGenerators));
        }
    }

    // Method to check for null and assign the value for URI type
    private void setUriValue(final SensorReport sensorReport, final BiConsumer<SensorReport, URI> setter, final URI uriValue) {
        if (uriValue != null) {
            setter.accept(sensorReport, uriValue);
        }
    }

    // Method to check for null and assign teh value for String type
    private void setStringValue(final SensorReport sensorReport, final BiConsumer<SensorReport, String> setter, final String stringValue) {
        if (stringValue != null) {
            setter.accept(sensorReport, stringValue);
        }
    }
}
