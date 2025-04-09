package io.openepcis.testdata.generator.template;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Setter
@Getter
@RegisterForReflection
public class CustomContextUrl implements Serializable {
  @Schema(type = SchemaType.NUMBER, description = "Unique identifier for the custom context.")
  private int ID;

  @Schema(type = SchemaType.STRING, description = "URL associated with the custom context.")
  private String contextURL;

  @Schema(type = SchemaType.STRING, description = "Prefix associated with the custom context URL.")
  private String prefix;

  @Schema(
      type = SchemaType.BOOLEAN,
      description =
          "Flag indicating whether the custom context should be included in generated events.")
  private Boolean isChecked;
}
