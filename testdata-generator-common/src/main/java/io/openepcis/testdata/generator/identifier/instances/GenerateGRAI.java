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
package io.openepcis.testdata.generator.identifier.instances;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.openepcis.testdata.generator.constants.DomainName;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.format.CompanyPrefixFormatter;
import io.openepcis.testdata.generator.format.RandomValueGenerator;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

@Setter
@JsonTypeName("grai")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateGRAI extends GenerateEPC {

  @Pattern(regexp = "^(\\s*|\\d{13})$", message = "GRAI should be of 14 digits")
  @NotNull(message = "GRAI cannot be null, it should consist of 14 digits")
  @Schema(type = SchemaType.STRING, description = "GRAI consisting of 14 digits", required = true)
  private String grai;

  private static final String GRAI_URN_PART = "urn:epc:id:grai:";
  private static final String GRAI_URI_PART = "/8003/";

  @Override
  public List<String> format(IdentifierVocabularyType syntax, Integer count) {
    if (syntax.equals(IdentifierVocabularyType.WEBURI)) {
      // For WebURI syntax call the generateWebURI, pass the required identifiers count to create
      // Instance Identifiers
      return generateWebURI(count);
    } else {
      // For URN syntax call the generateURN, pass the required identifiers count to create Instance
      // Identifiers
      return generateURN(count);
    }
  }

  // Generate URN formatted GRAI
  private List<String> generateURN(Integer count) {
    try {
      final List<String> formattedGRAI = new ArrayList<>();
      var modifiedGRAI = CompanyPrefixFormatter.gcpFormatterNormal(grai, gcpLength).toString();

      // Return the list of GRAI for RANGE calculation
      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.longValue() >= 0) {
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count;
            rangeID++) {
          formattedGRAI.add(GRAI_URN_PART + modifiedGRAI + "." + rangeID);
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        // Return the list of GRAI for RANDOM calculation
        randomMinLength = randomMinLength < 1 || randomMinLength > 16 ? 1 : randomMinLength;
        randomMaxLength = randomMaxLength < 1 || randomMaxLength > 16 ? 16 : randomMaxLength;

        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                randomType, randomMinLength, randomMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedGRAI.add(GRAI_URN_PART + modifiedGRAI + "." + randomID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null) {
        // Return the single GRAI values for None selection
        formattedGRAI.add(GRAI_URN_PART + modifiedGRAI + "." + serialNumber);
      }
      return formattedGRAI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GRAI instance identifiers in URN format, Please check the values provided for GRAI instance identifiers : "
              + ex.getMessage());
    }
  }

  // Generate WebURI formatted GRAI
  private List<String> generateWebURI(Integer count) {
    try {
      final List<String> formattedGRAI = new ArrayList<>();
      final String modifiedGRAI =
          this.grai.substring(0, 12) + UPCEANLogicImpl.calcChecksum(this.grai.substring(0, 12));

      // Return the list of GRAI for RANGE calculation
      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.longValue() >= 0) {
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count;
            rangeID++) {
          formattedGRAI.add(DomainName.IDENTIFIER_DOMAIN + GRAI_URI_PART + modifiedGRAI + rangeID);
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        // Return the list of GRAI for RANDOM calculation
        randomMinLength = randomMinLength < 1 || randomMinLength > 16 ? 1 : randomMinLength;
        randomMaxLength = randomMaxLength < 1 || randomMaxLength > 16 ? 16 : randomMaxLength;

        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                randomType, randomMinLength, randomMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedGRAI.add(DomainName.IDENTIFIER_DOMAIN + GRAI_URI_PART + modifiedGRAI + randomID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null) {
        // Return the single GRAI values for None selection
        formattedGRAI.add(
            DomainName.IDENTIFIER_DOMAIN + GRAI_URI_PART + modifiedGRAI + serialNumber);
      }
      return formattedGRAI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GRAI instance identifiers in WebURI format, Please check the values provided for GRAI instance identifiers : "
              + ex.getMessage());
    }
  }
}
