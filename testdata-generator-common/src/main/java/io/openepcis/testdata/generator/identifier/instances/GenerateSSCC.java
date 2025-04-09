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
package io.openepcis.testdata.generator.identifier.instances;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.RandomizationType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.identifier.util.RandomSerialNumberGenerator;
import io.openepcis.testdata.generator.identifier.util.SerialTypeChecker;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Setter
@JsonTypeName("sscc")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateSSCC extends GenerateEPCType2 {

    @Pattern(regexp = "^(\\s*|\\d)$", message = "Extension Digit must be between 0 and 9 (Ex: 0)")
    @Schema(type = SchemaType.NUMBER, description = "Extension Digit must be between 0 and 9 (Ex: 0)", required = true)
    private String extensionDigit = "0";

    private static final String SSCC_URN_PART = "urn:epc:id:sscc:";
    private static final String SSCC_URI_PART = "/00/";

    /**
     * Method to generate SSCC identifiers based on URN/WebURI format by manipulating the provided SSCC values.
     *
     * @param syntax                syntax in which identifiers need to be generated URN/WebURI
     * @param count                 count of SSCC instance identifiers need to be generated
     * @param dlURL                 if provided use the provided dlURI to format WebURI identifiers else use default ref.gs1.org
     * @param serialNumberGenerator instance of the RandomSerialNumberGenerator to generate random serial number
     * @return returns list of identifiers in string format
     */
    @Override
    public List<String> format(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final RandomSerialNumberGenerator serialNumberGenerator) {
        return generateIdentifiers(syntax, count, dlURL, serialNumberGenerator);
    }

    //Generate the SSCC identifiers based on the serialType and format them according to URN or WebURI format.
    private List<String> generateIdentifiers(final IdentifierVocabularyType syntax,
                                             final Integer count,
                                             final String dlURL,
                                             final RandomSerialNumberGenerator serialNumberGenerator) {
        try {
            final List<String> formattedSSCC = new ArrayList<>();
            final boolean isURN = IdentifierVocabularyType.URN.equals(syntax);
            final String prefix = isURN ? SSCC_URN_PART : dlURL + SSCC_URI_PART;
            final String modifiedGCP = isURN ? gcp : extensionDigit + gcp;
            final String delimiter = isURN ? "." + extensionDigit : "";
            final int maxAppendLength = isURN ? 18 : 17; // complete 18 digits for URN and 17 digits for WebURI excluding CheckDigit


            if (SerialTypeChecker.isRangeType(this.serialType, count, this.rangeFrom)) {
                // Generate range identifiers
                for (var rangeID = rangeFrom.longValue(); rangeID < rangeFrom.longValue() + count; rangeID++) {
                    // Generate serialReference based on rangeID and append 0s
                    final String serialReference = padAndAppend(modifiedGCP, delimiter, rangeID, maxAppendLength);

                    // Append with CheckDigit as last digit for the WebURI and for URN append directly without CheckDigit
                    formattedSSCC.add(buildIdentifier(prefix, modifiedGCP, delimiter, serialReference, isURN));
                }
                // Update rangeFrom for next iteration
                this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
            } else if (SerialTypeChecker.isRandomType(this.serialType, count)) {
                // Generate random identifiers
                final int requiredLength = maxAppendLength - (modifiedGCP.length() + delimiter.length());
                final List<String> randomSerialNumbers = serialNumberGenerator.randomGenerator(RandomizationType.NUMERIC, requiredLength, requiredLength, count);

                for (var randomID : randomSerialNumbers) {
                    // Append with CheckDigit as last digit for the WebURI and for URN append directly without CheckDigit
                    formattedSSCC.add(buildIdentifier(prefix, modifiedGCP, delimiter, randomID, isURN));
                }
            } else if (SerialTypeChecker.isNoneType(this.serialType, count, this.serialNumber)) {
                //For none generate static identifier

                // Generate serialReference based on serialNumber and append 0s
                final String serialReference = padAndAppend(modifiedGCP, delimiter, serialNumber, maxAppendLength);

                // Append with CheckDigit as last digit for the WebURI and for URN append directly without CheckDigit
                formattedSSCC.add(buildIdentifier(prefix, modifiedGCP, delimiter, serialReference, isURN));
            }
            return formattedSSCC;
        } catch (Exception ex) {
            throw new TestDataGeneratorException("Exception occurred during generation of SSCC instance identifiers : " + ex.getMessage(), ex);
        }
    }

    // Append leading zeros to the id to ensure the combined length with baseGCP matches maxAppendLength (18 for URN and 17 for WebURI)
    private String padAndAppend(final String baseGCP,
                                final String delimiter,
                                final Object id,
                                final int maxAppendLength) {
        return StringUtils.repeat("0", maxAppendLength - (baseGCP.length() + delimiter.length() + String.valueOf(id).length())) + id;
    }

    // Build the complete identifier with CheckDigit (WebURI) or without a CheckDigit (URN)
    private String buildIdentifier(final String prefix,
                                   final String modifiedGCP,
                                   final String delimiter,
                                   final String serialReference,
                                   final boolean isURN) {
        String identifier = prefix + modifiedGCP + delimiter + serialReference;
        if (!isURN) {
            // Add the CheckDigit as last digit for the WebURI format only
            final String checkDigit = String.valueOf(UPCEANLogicImpl.calcChecksum(modifiedGCP + serialReference));
            identifier += checkDigit;
        }
        return identifier;
    }
}
