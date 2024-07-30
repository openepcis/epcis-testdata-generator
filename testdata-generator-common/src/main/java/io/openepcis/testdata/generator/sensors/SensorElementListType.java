package io.openepcis.testdata.generator.sensors;

import io.openepcis.model.epcis.SensorMetadata;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@RegisterForReflection
@ToString
public class SensorElementListType {
    private SensorMetadata sensorMetadata;
    private List<SensorReportType> sensorReport;
}
