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

import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.math3.random.RandomDataGenerator;

@Getter
@Setter
@RequiredArgsConstructor
@RegisterForReflection
public class RandomDateTimeGenerator implements Serializable {

  private final OffsetDateTime fromTime;
  private final OffsetDateTime toTime;

  private RandomDataGenerator randomData = new RandomDataGenerator();

  public OffsetDateTime nextDate() {
    try {
      return Instant.ofEpochMilli(
              randomData.nextLong(
                  fromTime.toInstant().toEpochMilli(), toTime.toInstant().toEpochMilli()))
          .atZone(ZoneId.systemDefault())
          .toOffsetDateTime();
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during creation of Random Event Time, Please check the values provided values for EventTime : "
              + ex.getMessage());
    }
  }
}
