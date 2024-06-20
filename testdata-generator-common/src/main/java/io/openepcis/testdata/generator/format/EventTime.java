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
package io.openepcis.testdata.generator.format;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.identifier.util.RandomSerialNumberGenerator;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static com.fasterxml.jackson.annotation.JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@RegisterForReflection
public class EventTime implements Serializable {
  @JsonFormat(without = {ADJUST_DATES_TO_CONTEXT_TIME_ZONE})
  private OffsetDateTime specificTime;

  @JsonFormat(without = {ADJUST_DATES_TO_CONTEXT_TIME_ZONE})
  private OffsetDateTime fromTime;

  @JsonFormat(without = {ADJUST_DATES_TO_CONTEXT_TIME_ZONE})
  private OffsetDateTime toTime;

  @NotNull(message = "Timezone offset cannot be Null")
  private String timeZoneOffset;

  @Schema(type = SchemaType.INTEGER, description = "Custom offset From value for generating eventTime based on parent eventTime")
  private Integer customOffsetFrom;

  @Schema(type = SchemaType.INTEGER, description = "Custom offset To value for generating eventTime based on parent eventTime")
  private Integer customOffsetTo;

  @Schema(type = SchemaType.STRING, description = "Custom offset type value for generating eventTime based on parent eventTime ex: seconds/minutes/hours/days")
  private String customOffsetType;

  public OffsetDateTime generate(final OffsetDateTime parentEventTime, final RandomSerialNumberGenerator randomDateTimeGenerator) {
    try {
      if (specificTime != null) {
        //For specific time return provided date time
        return specificTime;
      } else if (fromTime != null && toTime != null) {
        //For range get the random date between from and to value using the Mersenne Twister
        return Instant.ofEpochMilli(randomDateTimeGenerator.generateIntInRange(fromTime.toInstant().toEpochMilli(), toTime.toInstant().toEpochMilli())).atZone(ZoneId.systemDefault()).toOffsetDateTime();
      } else if (parentEventTime != null && customOffsetFrom != null && customOffsetTo != null && StringUtils.isNotBlank(customOffsetType)) {
        //For custom offset from and to date time get the value based on the parentEventTime if parentEventTime is not null
        return calculateCustomOffset(parentEventTime, randomDateTimeGenerator);
      }

      //For any other non-matching case return the current date time
      return Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toOffsetDateTime();
    } catch (Exception ex) {
      throw new TestDataGeneratorException("Exception occurred during creation of assignment of the Event Time : " + ex.getMessage(), ex);
    }
  }

  //Method to calculate the eventTime based on custom offset values
  private OffsetDateTime calculateCustomOffset(final OffsetDateTime parentEventTime, final RandomSerialNumberGenerator randomDateTimeGenerator) {
    //Using from and to value generate a random number using Mersenne twister
    final long offsetValue = randomDateTimeGenerator.generateIntInRange(customOffsetFrom, customOffsetTo);

    //Append the Minutes/Hours/Days to the parent event eventTime based on the custom offset type value
    return switch (customOffsetType.toLowerCase()) {
      case "seconds" -> parentEventTime.plus(offsetValue, ChronoUnit.SECONDS);
      case "minutes" -> parentEventTime.plus(offsetValue, ChronoUnit.MINUTES);
      case "hours" -> parentEventTime.plus(offsetValue, ChronoUnit.HOURS);
      case "days" -> parentEventTime.plus(offsetValue, ChronoUnit.DAYS);
      default -> throw new IllegalArgumentException("Unsupported customOffsetType for eventTime generation : " + customOffsetType);
    };
  }
}
