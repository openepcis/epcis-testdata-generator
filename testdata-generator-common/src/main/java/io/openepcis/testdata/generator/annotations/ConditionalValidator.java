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
package io.openepcis.testdata.generator.annotations;

import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

@RegisterForReflection
@Slf4j
public class ConditionalValidator implements ConstraintValidator<ConditionalValidation, Object> {
  private String fieldName;
  private String[] expectedFieldValue;
  private String[] dependFieldName;
  private String message;

  @Override
  public void initialize(final ConditionalValidation annotation) {
    fieldName = annotation.fieldName();
    expectedFieldValue = annotation.fieldValue();
    dependFieldName = annotation.dependFieldName();
    message = annotation.message();
  }

  @Override
  public boolean isValid(final Object value, final ConstraintValidatorContext ctx) {
    if (value == null) {
      return true;
    }
    try {
      final String fieldValue = BeanUtils.getProperty(value, fieldName);

      // If the expected Field value is Null then show the error message mainly used for the
      // ReadPoint and BizLocation
      if (fieldValue == null || fieldValue.isEmpty()) {
        // If the required field value is null/empty and depending on field value is also empty then
        // call method to throw the error
        for (String depend : dependFieldName) {
          final String dependFieldValue = BeanUtils.getProperty(value, depend);
          if (dependFieldValue == null) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(depend)
                .addConstraintViolation();
            return false;
          }
        }
      } else {
        // If the expectedValue field has values then based on it check for matching values mainly
        // used for Source & Destination
        return validatorException(value, ctx);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new TestDataGeneratorException(e.getMessage());
    }
    return true;
  }

  // Method called when the expected field and desired field both contain Null values
  private boolean validatorException(final Object value, final ConstraintValidatorContext ctx) {
    try {
      final String fieldValue = BeanUtils.getProperty(value, fieldName);
      for (String expect : expectedFieldValue) {
        for (String depend : dependFieldName) {
          final String dependFieldValue = BeanUtils.getProperty(value, depend);
          if (expect.equals(fieldValue) && dependFieldValue == null) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(depend)
                .addConstraintViolation();
            return false;
          }
        }
      }
    } catch (Exception e) {
      throw new TestDataGeneratorException(e.getMessage());
    }
    return true;
  }
}
