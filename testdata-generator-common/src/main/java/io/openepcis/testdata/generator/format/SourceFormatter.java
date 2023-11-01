/*
 * Copyright 2022-2023 benelog GmbH & Co. KG
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

import io.openepcis.model.epcis.SourceList;
import io.openepcis.testdata.generator.constants.*;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

@RegisterForReflection
public class SourceFormatter implements Serializable {

  public static SourceList format(IdentifierVocabularyType syntax, SourceDestinationSyntax source) {
    // If the source is Not other type then based on provided values generate the Source/Destination
    if (!source.getType().toString().equalsIgnoreCase("other")) {
      if (IdentifierVocabularyType.WEBURI == syntax) {
        return formatWebURI(source);
      } else {
        return formatURN(source);
      }
    } else {
      // If the source is of OTHER type then based on provided custom Type/URI values format the
      // Source/Destination
      final var srcFormatted = new SourceList();
      srcFormatted.setType(source.getManualType() != null ? source.getManualType() : " ");
      srcFormatted.setSource(source.getManualURI() != null ? source.getManualURI() : " ");
      return srcFormatted;
    }
  }

  private static SourceList formatURN(SourceDestinationSyntax input) {
    try {
      final var srcFormatted = new SourceList();
      String gln = input.getGln();
      gln = gln.substring(0, 12) + UPCEANLogicImpl.calcChecksum(gln.substring(0, 12));
      gln = CompanyPrefixFormatter.gcpFormatterNormal(gln, input.getGcpLength()).toString();
      srcFormatted.setType(input.getType().toString().toLowerCase());

      if (input.getGlnType() == SourceDestinationGLNType.PGLN) {
        srcFormatted.setSource("urn:epc:id:pgln:" + gln);
      } else {
        if (input.getExtension() != null) {
          srcFormatted.setSource("urn:epc:id:sgln:" + gln + "." + input.getExtension());
        } else {
          srcFormatted.setSource("urn:epc:id:sgln:" + gln);
        }
      }
      return srcFormatted;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during formatting of the Source in URN format, Please check the values provided values for Source : "
              + ex.getMessage());
    }
  }

  private static SourceList formatWebURI(SourceDestinationSyntax input) {
    try {
      String gln = input.getGln();
      gln = gln.substring(0, 12) + UPCEANLogicImpl.calcChecksum(gln.substring(0, 12));
      String source = "";

      // For ProcessingParty and OwningParty add the 417 as application identifier.
      if (input.getType().equals(SourceDestinationType.POSSESSING_PARTY)
          || input.getType().equals(SourceDestinationType.OWNING_PARTY)) {
        source = DomainName.IDENTIFIER_DOMAIN + "/417/" + gln;
      } else if (input.getType().equals(SourceDestinationType.LOCATION)) {
        // For Location add the 414 as application identifier.
        source = DomainName.IDENTIFIER_DOMAIN + "/414/" + gln;
      }

      // If the extension is present then add the extension to the source else keep it blank.
      source =
          source
              + (input.getExtension() != null && !input.getExtension().isEmpty()
                  ? "/254/" + input.getExtension()
                  : "");

      // Add the formatted values to SourceList and return it to calling method.
      final var srcFormatted = new SourceList();
      srcFormatted.setType(input.getType().toString().toLowerCase());
      srcFormatted.setSource(source);

      return srcFormatted;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during formatting of the Source in WebURI format, Please check the values provided values for Source : "
              + ex.getMessage(), ex);
    }
  }
}
