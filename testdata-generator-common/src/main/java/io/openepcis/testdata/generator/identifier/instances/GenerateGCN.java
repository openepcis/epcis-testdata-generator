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
package io.openepcis.testdata.generator.identifier.instances;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.openepcis.testdata.generator.constants.DomainName;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.RandomizationType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.format.RandomValueGenerator;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

@Setter
@JsonTypeName("gcn")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateGCN extends GenerateEPCType2 {

  private static final String URN_SGCN_PART = "urn:epc:id:sgcn:";
  private static final String URI_SGCN_PART = "/255/";

  @Override
  public List<String> format(IdentifierVocabularyType syntax, Integer count) {
    if (IdentifierVocabularyType.WEBURI == syntax) {
      // For WebURI syntax call the generateWebURI, pass the required identifiers count to create
      // Instance Identifiers
      return generateWebURI(count);
    } else {
      // For URN syntax call the generateURN, pass the required identifiers count to create Instance
      // Identifiers
      return generateURN(count);
    }
  }

  private List<String> generateURN(Integer count) {
    try {
      List<String> formattedGCN = new ArrayList<>();

      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.longValue() >= 0) {
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count;
            rangeID++) {
          String append = gcp + rangeID;
          append = StringUtils.repeat("0", 23 - append.length()) + rangeID;
          append = gcp + append;
          final String finalSerial =
              gcp + "." + append.substring(gcp.length() + 1, 13) + "." + append.substring(14);
          final String gcnID = URN_SGCN_PART + finalSerial;
          formattedGCN.add(gcnID);
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        final int requiredLength = 23 - gcp.length();
        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                RandomizationType.NUMERIC, requiredLength, requiredLength, count);

        for (var randomID : randomSerialNumbers) {
          final String finalSerial =
              gcp + "." + randomID.substring(gcp.length() + 1, 13) + "." + randomID.substring(14);
          final String gcnID = URN_SGCN_PART + finalSerial;
          formattedGCN.add(gcnID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null && count != null) {
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          formattedGCN.add(URN_SGCN_PART + gcp + "." + serialNumber);
        }
      }
      return formattedGCN;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GCN instance identifiers in URN format, Please check the values provided for GCN instance identifiers : "
              + ex.getMessage(), ex);
    }
  }

  private List<String> generateWebURI(Integer count) {
    try {
      List<String> formattedGCN = new ArrayList<>();

      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.longValue() >= 0) {
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count;
            rangeID++) {
          var append = gcp + rangeID;
          append = StringUtils.repeat("0", 23 - append.length()) + rangeID;
          formattedGCN.add(DomainName.IDENTIFIER_DOMAIN + URI_SGCN_PART + gcp + append);
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        final int requiredLength = 23 - gcp.length();
        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                RandomizationType.NUMERIC, requiredLength, requiredLength, count);
        for (var randomID : randomSerialNumbers) {
          formattedGCN.add(DomainName.IDENTIFIER_DOMAIN + URI_SGCN_PART + gcp + randomID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null && count != null) {
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          formattedGCN.add(DomainName.IDENTIFIER_DOMAIN + URI_SGCN_PART + gcp + serialNumber);
        }
      }

      return formattedGCN;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GCN instance identifiers in WebURI format, Please check the values provided for GCN instance identifiers : "
              + ex.getMessage(), ex);
    }
  }
}
