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
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.RandomizationType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.format.RandomMersenneValueGenerator;
import io.openepcis.testdata.generator.identifier.util.SerialTypeChecker;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Setter
@JsonTypeName("gcn")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateGCN extends GenerateEPCType2 {

  private static final String URN_SGCN_PART = "urn:epc:id:sgcn:";
  private static final String URI_SGCN_PART = "/255/";

  /**
   * Method to generate identifiers based on URN/WebURI format by manipulating the provided values.
   *
   * @param syntax syntax in which identifiers need to be generated URN/WebURI
   * @param count  count of instance identifiers need to be generated
   * @param dlURL  if provided use the provided dlURI to format WebURI identifiers else use default ref.gs1.org
   * @param seed   seed for random mersenne generator to generate same random numbers if same seed is provided
   * @return returns list of identifiers in string format
   */
  @Override
  public List<String> format(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final Long seed) {
    return generateIdentifiers(syntax, count, dlURL, seed);
  }

  private List<String> generateIdentifiers(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final Long seed) {
    try {
      final List<String> formattedGCN = new ArrayList<>();
      final String prefix = syntax.equals(IdentifierVocabularyType.URN) ? URN_SGCN_PART : dlURL + URI_SGCN_PART;
      final String suffix = syntax.equals(IdentifierVocabularyType.URN) ? "." : "";

      if (SerialTypeChecker.isRangeType(serialType, count, rangeFrom)) {
        //For range generate sequential identifiers
        for (var rangeID = rangeFrom.longValue(); rangeID < rangeFrom.longValue() + count; rangeID++) {
          String append = gcp + rangeID;
          append = gcp + StringUtils.repeat("0", 23 - append.length()) + rangeID;
          formattedGCN.add(prefix + generateSerialNumber(suffix, append));
        }
        rangeFrom = BigInteger.valueOf(rangeFrom.longValue() + count);
      } else if (SerialTypeChecker.isRandomType(serialType, count)) {
        //For random generate random identifiers or based on seed
        final int requiredLength = 23 - gcp.length();
        final List<String> randomSerialNumbers = RandomMersenneValueGenerator.getInstance(seed).randomGenerator(RandomizationType.NUMERIC, requiredLength, requiredLength, count);
        for (var randomID : randomSerialNumbers) {
          formattedGCN.add(prefix + generateSerialNumber(suffix, gcp + randomID));
        }
      } else if (SerialTypeChecker.isNoneType(serialType, count, serialNumber)) {
        //For none generate static identifier
        String append = gcp + serialNumber;
        append = gcp + StringUtils.repeat("0", 23 - append.length()) + serialNumber;
        formattedGCN.add(prefix + generateSerialNumber(suffix, append));
      } return formattedGCN;
    } catch (Exception ex) {
      throw new TestDataGeneratorException("Exception occurred during generation of GCN instance identifiers : " + ex.getMessage(), ex);
    }
  }

  //Method to append the serial number to gcp with formatting as per GCN
  private String generateSerialNumber(final String suffix, final String append) {
    return gcp + suffix + append.substring(gcp.length() + 1, 13) + suffix + append.substring(14);
  }

}