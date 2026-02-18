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
import org.apache.commons.lang3.StringUtils;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Setter
@JsonTypeName("gsrnp")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateGSRNP extends GenerateEPCType2 {

    private static final String GSRNP_URN_PART = "urn:epc:id:gsrnp:";
    private static final String GSRNP_URI_PART = "/8017/";

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

    //Function to check which type of instance identifiers need to be generated Range/Random/Static
    private List<String> generateIdentifiers(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final RandomSerialNumberGenerator serialNumberGenerator) {
        try {
            final List<String> formattedGSRNP = new ArrayList<>();
            final boolean isURN = IdentifierVocabularyType.URN.equals(syntax);
            final String prefix = isURN ? GSRNP_URN_PART : dlURL + GSRNP_URI_PART;
            final String delimiter = isURN ? "." : "";
            final int maxAppendLength = isURN ? 18 : 17;

            if (SerialTypeChecker.isRangeType(serialType, count, rangeFrom)) {
                // Generate SEQUENTIAL/RANGE identifiers in URN/WEBURI format based on from value and count
                for (var rangeID = rangeFrom.longValue(); rangeID < rangeFrom.longValue() + count; rangeID++) {
                    // Generate serviceReference based on rangeID and append 0s
                    final String serviceReference = padAndAppend(gcp, delimiter, rangeID, maxAppendLength);

                    // Append with CheckDigit as last digit for the WebURI and for URN append directly without CheckDigit
                    formattedGSRNP.add(buildIdentifier(prefix, gcp, delimiter, serviceReference, isURN));
                }
                // Update rangeFrom for next iteration
                rangeFrom = BigInteger.valueOf(rangeFrom.longValue() + count);
            } else if (SerialTypeChecker.isRandomType(serialType, count)) {
                // Generate RANDOM identifiers in URN/WEBURI format based on count
                final int requiredLength = maxAppendLength - (gcp.length() + delimiter.length());
                final List<String> randomSerialNumbers = serialNumberGenerator.randomGenerator(RandomizationType.NUMERIC, requiredLength, requiredLength, count);

                for (var randomID : randomSerialNumbers) {
                    // Append with CheckDigit as last digit for the WebURI and for URN append directly without CheckDigit
                    formattedGSRNP.add(buildIdentifier(prefix, gcp, delimiter, randomID, isURN));
                }
            } else if (SerialTypeChecker.isNoneType(serialType, count, serialNumber)) {
                //For none generate static identifier

                // Generate serviceReference based on serialNumber and append 0s
                final String serviceReference = padAndAppend(gcp, delimiter, serialNumber, maxAppendLength);

                // Append with CheckDigit as last digit for the WebURI and for URN append directly without CheckDigit
                formattedGSRNP.add(buildIdentifier(prefix, gcp, delimiter, serviceReference, isURN));
            }
        
            return formattedGSRNP;
        } catch (Exception ex) {
            throw new TestDataGeneratorException("Exception occurred during generation of GSRNP instance identifiers : " + ex.getMessage(), ex);
        }
    }

    // Append leading zeros to the id to ensure the combined length with baseGCP matches totalLength (18 for URN and 17 for WebURI)
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
                                   final String serviceReference,
                                   final boolean isURN) {
        String identifier = prefix + modifiedGCP + delimiter + serviceReference;
        if (!isURN) {
            // Add the CheckDigit as last digit for the WebURI format only
            final String checkDigit = String.valueOf(UPCEANLogicImpl.calcChecksum(modifiedGCP + serviceReference));
            identifier += checkDigit;
        }
        return identifier;
    }
}
