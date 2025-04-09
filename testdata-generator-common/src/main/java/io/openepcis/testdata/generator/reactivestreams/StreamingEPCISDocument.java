/*
 * Copyright 2022-2024 benelog GmbH & Co. KG
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package io.openepcis.testdata.generator.reactivestreams;

import io.openepcis.model.epcis.EPCISEvent;
import io.openepcis.testdata.generator.format.UserExtensionSyntax;
import io.openepcis.testdata.generator.template.CustomContextUrl;
import io.openepcis.testdata.generator.template.EPCISEventType;
import io.openepcis.testdata.generator.template.ObjectEventType;
import io.openepcis.testdata.generator.template.TransformationEventType;
import io.smallrye.mutiny.Multi;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class StreamingEPCISDocument {
  // Variable to store the localname & namespaces from User Extensions so can be added to @context
  @Getter private static Map<String, String> context;

  @Getter private static List<String> selectedContextUrls;

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

  public static void storeContextUrls(final List<CustomContextUrl> customContextUrls) {
    if (CollectionUtils.isNotEmpty(customContextUrls)) {
      selectedContextUrls =
          customContextUrls.stream()
              .filter(c -> Objects.nonNull(c.getIsChecked()) && c.getIsChecked())
              .map(CustomContextUrl::getContextURL)
              .toList();
    }
  }

  /**
   * Extract namespaceURI and localname from each event and store in context.
   *
   * @param extensions list of extensions from ilmd, error and userExtensions
   */
  public static void extractNamespaces(final List<UserExtensionSyntax> extensions) {
    extensions.forEach(UserExtensionSyntax::extractNamespaces);
  }

  /**
   * write to OutputStream
   *
   * @param fn builder reference
   * @throws IOException throws the io exception
   */
  public void writeToOutputStream(
      Function<StreamingEPCISDocumentOutput.OutputStreamBuilder, StreamingEPCISDocumentOutput> fn)
      throws IOException {
    fn.apply(StreamingEPCISDocumentOutput.outputStreamBuilder()).write(this);
  }

  /**
   * write to Writer
   *
   * @param fn builder reference
   * @throws IOException throws the io exception
   */
  public void writeToWriter(
      Function<StreamingEPCISDocumentOutput.WriterBuilder, StreamingEPCISDocumentOutput> fn)
      throws IOException {
    fn.apply(StreamingEPCISDocumentOutput.writerBuilder()).write(this);
  }
}
