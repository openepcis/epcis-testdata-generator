/*
 * Copyright 2022 benelog GmbH & Co. KG
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
package io.openepcis.testdata.generator.format;

import io.openepcis.model.epcis.DestinationList;
import io.openepcis.testdata.generator.constants.*;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RegisterForReflection
public class DestinationFormatter {

  public static DestinationList format(
      IdentifierVocabularyType syntax, SourceDestinationSyntax destination) {
    // If the source is Not other type then based on provided values generate the Source/Destination
    if (!destination.getType().toString().equalsIgnoreCase("other")) {
      if (IdentifierVocabularyType.WEBURI == syntax) {
        return formatWebURI(destination);
      } else {
        return formatURN(destination);
      }
    } else {
      // If the source is of OTHER type then based on provided custom Type/URI values format the
      // Source/Destination
      final var dstFormatted = new DestinationList();
      dstFormatted.setType(destination.getManualType() != null ? destination.getManualType() : " ");
      dstFormatted.setDestination(
          destination.getManualURI() != null ? destination.getManualURI() : " ");
      return dstFormatted;
    }
  }

  private static DestinationList formatURN(SourceDestinationSyntax input) {
    try {
      final var dstFormatted = new DestinationList();
      String gln = input.getGln();
      gln = gln.substring(0, 12) + UPCEANLogicImpl.calcChecksum(gln.substring(0, 12));
      gln = CompanyPrefixFormatter.gcpFormatterNormal(gln, input.getGcpLength()).toString();
      dstFormatted.setType(input.getType().toString().toLowerCase());

      if (input.getGlnType() == SourceDestinationGLNType.PGLN) {
        dstFormatted.setDestination("urn:epc:id:pgln:" + gln);
      } else {
        if (input.getExtension() != null) {
          dstFormatted.setDestination("urn:epc:id:sgln:" + gln + "." + input.getExtension());
        } else {
          dstFormatted.setDestination("urn:epc:id:sgln:" + gln);
        }
      }
      return dstFormatted;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during formatting of the Destination URN, Please check the values provided for Destination : "
              + ex.getMessage());
    }
  }

  private static DestinationList formatWebURI(SourceDestinationSyntax input) {
    try {

      String gln = input.getGln();
      gln = gln.substring(0, 12) + UPCEANLogicImpl.calcChecksum(gln.substring(0, 12));
      String destination = "";

      // For ProcessingParty and OwningParty add the 417 as application identifier.
      if (input.getType().equals(SourceDestinationType.PROCESSING_PARTY)
          || input.getType().equals(SourceDestinationType.OWNING_PARTY)) {
        destination = DomainName.IDENTIFIER_DOMAIN + "/417/" + gln;
      } else if (input.getType().equals(SourceDestinationType.LOCATION)) {
        // For Location add the 414 as application identifier.
        destination = DomainName.IDENTIFIER_DOMAIN + "/414/" + gln;
      }

      // If the extension is present then add the extension to the destination else keep it blank.
      destination =
          destination
              + (input.getExtension() != null && !input.getExtension().isEmpty()
                  ? "/254/" + input.getExtension()
                  : "");

      // Add the formatted values to DestinationList and return it to calling method.
      final var dstFormatted = new DestinationList();
      dstFormatted.setType(input.getType().toString().toLowerCase());
      dstFormatted.setDestination(destination);

      return dstFormatted;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during formatting of the Destination WebURI, Please check the values provided for Destination : "
              + ex.getMessage());
    }
  }
}
