package io.openepcis.testdata.generator.format;

import io.openepcis.testdata.generator.template.EPCISEventType;
import io.openepcis.testdata.generator.template.ObjectEventType;
import io.openepcis.testdata.generator.template.TransformationEventType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContextNamespaceBuilder {
  // Variable to store the localname & namespaces from User Extensions, ILMD and Error Extensions so
  // can be added to @context
  @Getter private static List<Object> context;

  public static void storeContextInfo(final List<EPCISEventType> events) {
    // Empty the context list for the next event to store the namespaces corresponding to the event
    context = new ArrayList<>();
    context.add("https://ref.gs1.org/standards/epcis/2.0.0/epcis-context.jsonld");

    events.forEach(
        typeInfo -> {
          // Store information related User Extensions
          if (typeInfo.getUserExtensions() != null && !typeInfo.getUserExtensions().isEmpty()) {
            extractNamespaces(typeInfo.getUserExtensions());
          }

          // For ObjectEvent and TransformationEvent store the information related to ILMD elements
          if (typeInfo instanceof ObjectEventType objectEventType
              && objectEventType.getIlmd() != null
              && !objectEventType.getIlmd().isEmpty()) {
            // Store information related to ObjectEvent ILMD
            extractNamespaces(objectEventType.getIlmd());
          } else if (typeInfo instanceof TransformationEventType transformationEventType
              && transformationEventType.getIlmd() != null
              && !transformationEventType.getIlmd().isEmpty()) {
            // Store information related to TransformationEvent ILMD
            extractNamespaces(transformationEventType.getIlmd());
          }

          // Store the information related to Error extensions
          if (typeInfo.getErrorDeclaration() != null
              && typeInfo.getErrorDeclaration().getExtensions() != null
              && !typeInfo.getErrorDeclaration().getExtensions().isEmpty()) {
            extractNamespaces(typeInfo.getErrorDeclaration().getExtensions());
          }
        });
  }

  public static void extractNamespaces(final List<UserExtensionSyntax> extensions) {
    extensions.stream()
        .flatMap(c -> c.toMap().entrySet().stream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (r1, r2) -> r1));
  }
}
