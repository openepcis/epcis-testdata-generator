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
import io.openepcis.testdata.generator.identifier.util.RandomSerialNumberGenerator;
import io.openepcis.testdata.generator.identifier.util.SerialTypeChecker;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Setter
@JsonTypeName("adi")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateADI implements EPCStrategy {

  @Pattern(regexp = "^[A-Z0-9]+$", message = "ADI CAGE/DoDAAC should be Min 5 digits and Max 6 digits")
  @NotNull(message = "ADI cage cannot be Null, It should be Min 5 digits and Max 6 digits")
  @Schema(type = SchemaType.STRING, description = "ADI CAGE/DoDAAC with min 5 digits and max 6 digits", required = true)
  private String adiCage;

  @Pattern(regexp = "^([\\x30-\\x39\\x41-\\x5A]{0,20})$", message = "ADI PNO should be Min 0 characters and max 20 characters")
  @Schema(type = SchemaType.STRING, description = "ADI PNO with min 0 digits and max 20 digits")
  private String adiPNO;

  @NotNull(message = "Serial Numbers type can not be null")
  @Schema(type = SchemaType.STRING, description = "Type of serial identifier generation", enumeration = {"range", "random", "none"}, required = true)
  protected String serialType;

  @Pattern(regexp = "^([\\x30-\\x39\\x41-\\x5A]{1,20})$", message = "Invalid Serial number for ADI identifiers")
  @Schema(type = SchemaType.STRING, description = "Serial number for none based identifier generation")
  private String serialNumber;

  @Min(value = 0, message = "Range start value cannot be less than 0")
  @Schema(type = SchemaType.NUMBER, description = "Starting value for range based identifier generation")
  private BigInteger rangeFrom;

  @Min(value = 1, message = "Range end value cannot be less than 1")
  @Schema(type = SchemaType.NUMBER, description = "Ending value for range based identifier generation")
  private Integer rangeTo;

  @Min(value = 1, message = "Number of required serial numbers cannot be less than 1")
  @Schema(type = SchemaType.NUMBER, description = "Count value for random based identifier generation")
  private Integer randomCount;

  @Schema(type = SchemaType.NUMBER, description = "Min character length for random serial numbers.")
  protected Integer randomMinLength;

  @Schema(type = SchemaType.NUMBER, description = "Max character length for random serial numbers.")
  protected Integer randomMaxLength;

  private static final String ADI_URN_PART = "urn:epc:id:adi:";

  @Override
  public List<String> format(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final RandomSerialNumberGenerator serialNumberGenerator) {
    return generateURN(count, serialNumberGenerator);
  }

  private List<String> generateURN(final Integer count, final RandomSerialNumberGenerator serialNumberGenerator) {
    try {
      final List<String> formattedADI = new ArrayList<>();
      final String suffix = ".";

      if (SerialTypeChecker.isRangeType(serialType, count, rangeFrom)) {
        //For range generate sequential identifiers
        for (var rangeID = rangeFrom.longValue(); rangeID < rangeFrom.longValue() + count; rangeID++) {
          formattedADI.add(ADI_URN_PART + adiCage + suffix + adiPNO + suffix + rangeID);
        }
        rangeFrom = BigInteger.valueOf(rangeFrom.longValue() + count);
      } else if (SerialTypeChecker.isRandomType(serialType, count)) {
        //For random generate random identifiers or based on seed
        randomMinLength = Math.max(1, Math.min(20, randomMinLength));
        randomMaxLength = Math.max(1, Math.min(20, randomMaxLength));

        final List<String> randomSerialNumbers = serialNumberGenerator.randomGenerator(RandomizationType.NUMERIC, randomMinLength, randomMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedADI.add(ADI_URN_PART + adiCage + suffix + adiPNO + suffix + randomID);
        }
      } else if (SerialTypeChecker.isNoneType(serialType, count, serialNumber)) {
        //For none generate static identifier
        formattedADI.add(ADI_URN_PART + adiCage + suffix + adiPNO + suffix + serialNumber);
      }
      return formattedADI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException("Exception occurred during generation of ADI instance identifiers : " + ex.getMessage(), ex);
    }
  }
}
