package io.openepcis.testdata.generator.constants;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public enum ReadPointBizLocationType {
    SGLN("SGLN"),
    MANUALLY("MANUALLY");

    private String type;

    ReadPointBizLocationType(String type) {
        this.type = type;
    }

    String getReadPointBizLocationType() {
        return type;
    }
}
