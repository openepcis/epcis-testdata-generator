package io.openepcis.testdata.generator.reactivestreams;

import io.openepcis.model.epcis.EPCISEvent;
import io.openepcis.testdata.generator.format.UserExtensionSyntax;
import io.openepcis.testdata.generator.template.EPCISEventType;
import io.openepcis.testdata.generator.template.ObjectEventType;
import io.openepcis.testdata.generator.template.TransformationEventType;
import io.smallrye.mutiny.Multi;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class StreamingEPCISDocument {
    // Variable to store the localname & namespaces from User Extensions so can be added to @context
    @Getter
    private static Map<String, String> context;

    private Multi<EPCISEvent> epcisEvents;
    private boolean prettyPrint;

    public static void storeContextInfo(final List<EPCISEventType> events) {
        // Empty the context list for the next event to store the namespaces corresponding to the event
        context = new HashMap<>();
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

    /**
     * write to OutputStream
     * @param fn builder reference
     * @throws IOException
     */
    public void writeToOutputStream(Function<StreamingEPCISDocumentOutput.OutputStreamBuilder, StreamingEPCISDocumentOutput> fn) throws IOException {
        fn.apply(StreamingEPCISDocumentOutput.outputStreamBuilder()).writeTo(this);
    }

    /**
     * write to Writer
     * @param fn builder reference
     * @throws IOException
     */
    public void writeToWriter(Function<StreamingEPCISDocumentOutput.WriterBuilder, StreamingEPCISDocumentOutput> fn) throws IOException {
        fn.apply(StreamingEPCISDocumentOutput.writerBuilder()).writeTo(this);
    }

}
