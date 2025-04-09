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
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Setter
@ToString(callSuper = true)
@JsonTypeName("gsin")
@RegisterForReflection
public class GenerateGSIN extends GenerateEPCType2 {

    private static final String GSIN_URN_PART = "urn:epc:id:gsin:";
    private static final String GSIN_URI_PART = "/402/";

    /**
     * Method to generate identifiers based on URN/WebURI format by manipulating the provided values.
     *
     * @param syntax                syntax in which identifiers need to be generated URN/WebURI
     * @param count                 count of instance identifiers need to be generated
     * @param dlURL                 if provided use the provided dlURI to format WebURI identifiers else use default ref.gs1.org
     * @param serialNumberGenerator instance of the RandomSerialNumberGenerator to generate random serial number
     * @return returns list of identifiers in string format
     */
    @Override
    public List<String> format(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final RandomSerialNumberGenerator serialNumberGenerator) {
        return generateIdentifiers(syntax, count, dlURL, serialNumberGenerator);
    }

    private List<String> generateIdentifiers(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final RandomSerialNumberGenerator serialNumberGenerator) {
        try {
            final List<String> formattedGSIN = new ArrayList<>();
            final boolean isURN = IdentifierVocabularyType.URN.equals(syntax);
            final String prefix = isURN ? GSIN_URN_PART : dlURL + GSIN_URI_PART;
            final String delimiter = isURN ? "." : "";
            final int maxAppendLength = isURN ? 17 : 16;

            if (SerialTypeChecker.isRangeType(serialType, count, rangeFrom)) {
                //For range generate sequential identifiers
                for (var rangeID = rangeFrom.longValue(); rangeID < rangeFrom.longValue() + count.longValue(); rangeID++) {
                    // Generate shippingReference based on rangeID and append 0s
                    final String shippingReference = padAndAppend(gcp, delimiter, rangeID, maxAppendLength);

                    // Append with CheckDigit as last digit for the WebURI and for URN append directly without CheckDigit
                    formattedGSIN.add(buildIdentifier(prefix, gcp, delimiter, shippingReference, isURN));
                }
                // Update rangeFrom for next iteration
                rangeFrom = BigInteger.valueOf(rangeFrom.longValue() + count.longValue());
            } else if (SerialTypeChecker.isRandomType(serialType, count)) {
                //For random generate random identifiers or based on seed
                final int requiredLength = maxAppendLength - (gcp.length() + delimiter.length());
                final List<String> randomSerialNumbers = serialNumberGenerator.randomGenerator(RandomizationType.NUMERIC, requiredLength, requiredLength, count);

                for (var randomID : randomSerialNumbers) {
                    // Generate shippingReference based on randomID and append 0s
                    final String shippingReference = padAndAppend(gcp, delimiter, randomID, maxAppendLength);

                    // Append with CheckDigit as last digit for the WebURI and for URN append directly without CheckDigit
                    formattedGSIN.add(buildIdentifier(prefix, gcp, delimiter, shippingReference, isURN));
                }
            } else if (SerialTypeChecker.isNoneType(serialType, count, serialNumber)) {
                //For none generate static identifier

                // Generate serialReference based on serialNumber and append 0s to make it 17 digits
                final String serialReference = padAndAppend(gcp, delimiter, serialNumber, maxAppendLength);

                // Append with CheckDigit as last digit for the WebURI and for URN append directly without CheckDigit
                formattedGSIN.add(buildIdentifier(prefix, gcp, delimiter, serialReference, isURN));
            }

            return formattedGSIN;
        } catch (Exception ex) {
            throw new TestDataGeneratorException("Exception occurred during generation of GSIN instance identifiers : " + ex.getMessage(), ex);
        }
    }

    // Append leading zeros to the id to ensure the combined length matches maxAppendLength (17 for URN and 16 for WebURI)
    private String padAndAppend(final String baseGcp,
                                final String delimiter,
                                final Object id,
                                final int maxAppendLength) {
        return StringUtils.repeat("0", maxAppendLength - (baseGcp.length() + delimiter.length() + String.valueOf(id).length())) + id;
    }

    // Build the complete identifier with CheckDigit (WebURI) or without a CheckDigit (URN)
    private String buildIdentifier(final String prefix,
                                   final String gcp,
                                   final String delimiter,
                                   final String shippingReference,
                                   final boolean isURN) {
        String identifier = prefix + gcp + delimiter + shippingReference;
        if (!isURN) {
            // Add the CheckDigit as last digit for the WebURI format only
            final String checkDigit = String.valueOf(UPCEANLogicImpl.calcChecksum(gcp + shippingReference));
            identifier += checkDigit;
        }
        return identifier;
    }
}
