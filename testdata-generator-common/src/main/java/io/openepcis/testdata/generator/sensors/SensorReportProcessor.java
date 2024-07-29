package io.openepcis.testdata.generator.sensors;

import io.openepcis.model.epcis.SensorReport;
import io.openepcis.testdata.generator.format.ValueTypeSyntax;
import io.openepcis.testdata.generator.template.RandomGenerators;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SensorReportProcessor {
    public List<SensorReport> processSensorReports(final List<SensorReportType> reports, final List<RandomGenerators> randomGenerators) throws Exception {
        List<SensorReport> sensorReports = new ArrayList<>();

        for (final SensorReportType report : reports) {
            final SensorReport sensorReport = new SensorReport();
            final Field[] fields = report.getClass().getDeclaredFields();

            for (final Field field : fields) {
                if (field.getType().equals(ValueTypeSyntax.class)) {
                    field.setAccessible(true);
                    final ValueTypeSyntax valueTypeSyntax = (ValueTypeSyntax) field.get(report);
                    if (valueTypeSyntax != null) {
                        double value = valueTypeSyntax.getValue(randomGenerators);

                        Field targetField = SensorReport.class.getDeclaredField(field.getName());
                        targetField.setAccessible(true);
                        targetField.set(sensorReport, value);
                    }
                }
            }
            sensorReports.add(sensorReport);
        }
        return sensorReports;
    }
}
