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

import static com.fasterxml.jackson.annotation.JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

  private RandomDateTimeGenerator randomDateTimeGenerator;

  public OffsetDateTime generate() {
    try {
      if (specificTime != null) {
        return specificTime;
      }

      if (randomDateTimeGenerator == null) {
        randomDateTimeGenerator = new RandomDateTimeGenerator(fromTime, toTime);
      }

      return randomDateTimeGenerator.nextDate();
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during creation of assignment of the Event Time, Please check the values provided values for EventTime : "
              + ex.getMessage(), ex);
    }
  }
}
