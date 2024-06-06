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
@JsonTypeName("usdod")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateUSDoD implements EPCStrategy {

  @Pattern(regexp = "^[A-Z0-9]{5,6}$", message = "USDoD CAGE/DoDAAC should be Min 5 digits and Max 6 digits")
  @NotNull(message = "Cage value for USDoD cannot be null, it should be Min 5 digits and Max 6 digits")
  @Schema(type = SchemaType.STRING, description = "Cage value for USDoD should be Min 5 digits and Max 6 digits", required = true)
  private String usdodCage;

  @NotNull(message = "Serial Numbers type can not be null.")
  @Schema(type = SchemaType.STRING, description = "Type of serial identifier generation.", enumeration = {"range", "random", "none"}, required = true)
  private String serialType;

  @Min(value = 0, message = "Range start value cannot be less than 0.")
  @Schema(type = SchemaType.NUMBER, description = "Starting value for range based identifier generation.")
  protected BigInteger rangeFrom;

  @Min(value = 1, message = "Range end value cannot be less than 1.")
  @Schema(type = SchemaType.NUMBER, description = "Ending value for range based identifier generation.")
  protected Integer rangeTo;

  @Min(value = 1, message = "Number of required serial numbers cannot be less than 1.")
  @Schema(type = SchemaType.NUMBER, description = "Count value for random based identifier generation.")
  protected Integer randomCount;

  @Schema(type = SchemaType.NUMBER, description = "Min character length for random serial numbers.")
  protected Integer randomMinLength;

  @Schema(type = SchemaType.NUMBER, description = "Max character length for random serial numbers.")
  protected Integer randomMaxLength;

  @Schema(type = SchemaType.STRING, description = "Serial number for identifier generation.")
  protected String serialNumber;


  private static final String USDOD_URN_PART = "urn:epc:id:usdod:";

  @Override
  public List<String> format(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final RandomSerialNumberGenerator serialNumberGenerator) {
    return generateURN(count, serialNumberGenerator);
  }

  private List<String> generateURN(final Integer count, final RandomSerialNumberGenerator serialNumberGenerator) {
    try {
      final List<String> formattedUSDoD = new ArrayList<>();

      if (SerialTypeChecker.isRangeType(serialType, count, rangeFrom)) {
        //For range generate sequential identifiers
        for (var rangeID = rangeFrom.longValue(); rangeID < rangeFrom.longValue() + count; rangeID++) {
          formattedUSDoD.add(USDOD_URN_PART + usdodCage + "." + rangeID);
        }
        rangeFrom = BigInteger.valueOf(rangeFrom.longValue() + count);
      } else if (SerialTypeChecker.isRandomType(serialType, count)) {
        //For random generate random identifiers or based on seed
        randomMinLength = Math.max(1, Math.min(11, randomMinLength));
        randomMaxLength = Math.max(1, Math.min(11, randomMaxLength));

        final List<String> randomSerialNumbers = serialNumberGenerator.randomGenerator(RandomizationType.NUMERIC, randomMinLength, randomMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedUSDoD.add(USDOD_URN_PART + usdodCage + "." + randomID);
        }
      } else if (SerialTypeChecker.isNoneType(serialType, count, serialNumber)) {
        //For none generate static identifier
        formattedUSDoD.add(USDOD_URN_PART + usdodCage + "." + serialNumber);
      }
      return formattedUSDoD;
    } catch (Exception ex) {
      throw new TestDataGeneratorException("Exception occurred during generation of USDoD instance identifiers : " + ex.getMessage(), ex);
    }
  }
}
