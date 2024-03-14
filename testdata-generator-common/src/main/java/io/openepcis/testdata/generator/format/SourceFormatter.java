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
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.SourceDestinationGLNType;
import io.openepcis.testdata.generator.constants.SourceDestinationType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.commons.lang3.StringUtils;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

import java.io.Serializable;

@RegisterForReflection
public class SourceFormatter implements Serializable {

  public static SourceList format(IdentifierVocabularyType syntax, SourceDestinationSyntax source, final String dlURL) {
    // If the source is Not other type then based on provided values generate the Source/Destination
    if (!source.getType().toString().equalsIgnoreCase("other")) {
      if (IdentifierVocabularyType.WEBURI == syntax) {
        return formatWebURI(source, dlURL);
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

      //If type is LOCATION or GLN type is SGLN then format with SGLN and add extension if not null
      if (input.getType().equals(SourceDestinationType.LOCATION) || input.getGlnType() == SourceDestinationGLNType.SGLN) {
        if (input.getExtension() != null) {
          srcFormatted.setSource("urn:epc:id:sgln:" + gln + "." + input.getExtension());
        } else {
          srcFormatted.setSource("urn:epc:id:sgln:" + gln);
        }
      } else if(StringUtils.isNotBlank(input.getGlnType().toString()) && input.getGlnType().equals(SourceDestinationGLNType.PGLN)) {
        //if type is PGLN then format with PGLN
        srcFormatted.setSource("urn:epc:id:pgln:" + gln);
      }

      return srcFormatted;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during formatting of the Source in URN format, Please check the values provided values for Source : "
              + ex.getMessage());
    }
  }

  private static SourceList formatWebURI(SourceDestinationSyntax input, final String dlURL) {
    try {
      String gln = input.getGln();
      gln = gln.substring(0, 12) + UPCEANLogicImpl.calcChecksum(gln.substring(0, 12));
      String source = "";

      //If type is LOCATION or GLN type SGLN then format as per SGLN with /414/ and add extension if not NULL or 0
      if (input.getType().equals(SourceDestinationType.LOCATION) || input.getGlnType().equals(SourceDestinationGLNType.SGLN)) {
        source = dlURL + "/414/" + gln;

        //For SGLN type add the extension if provided and non-zero
        source += StringUtils.isNotBlank(input.getExtension()) && !input.getExtension().equals("0") ? "/254/" + input.getExtension() : "";
      } else if (StringUtils.isNotBlank(input.getGlnType().toString()) && input.getGlnType().equals(SourceDestinationGLNType.PGLN)) {
        //If GLN type is PGLN then format as per PGLN with /417/ and without extension
        source = dlURL + "/417/" + gln;
      }

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
