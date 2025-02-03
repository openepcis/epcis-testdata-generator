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
package io.openepcis.testdata.generator.template;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Getter
@Setter
@ToString
@RegisterForReflection
public class InputTemplate {
    @Schema(
            type = SchemaType.ARRAY,
            description = "List of events information required for creation of EPCIS events.",
            required = true)
    private List<@Valid EPCISEventType> events;

    @Schema(
            type = SchemaType.ARRAY,
            description = "List of identifiers information required for creation of EPCIS events.",
            required = true)
    private List<@Valid Identifier> identifiers;

    @Schema(
            type = SchemaType.ARRAY,
            description = "List of random types information required for creation of random values/serials.")
    private List<@Valid RandomGenerators> randomGenerators;

    @Schema(
            type = SchemaType.ARRAY,
            description = "A list of custom context URL definitions, used to extend the @context field in generated events.")
    private List<@Valid CustomContextUrl> contextUrls;
}
