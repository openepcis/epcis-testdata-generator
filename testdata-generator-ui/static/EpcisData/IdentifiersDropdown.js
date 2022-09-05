// OpenEPCIS Testdata Generator UI
// Copyright (C) 2022  benelog GmbH & Co. KG 
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// 
// See LICENSE in the project root for license information.
exports.InstanceIdentifierTypes = [
  { value: null, text: 'Instance Identifier Type', class: 'bold' },
  { value: 'GS1 Key', text: 'GS1 Key', class: 'bold', disabled: true },
  { value: 'sgtin', text: 'SGTIN (Al 01 + Al 21)' },
  { value: 'sscc', text: 'SSCC (Al 00)' },
  { value: 'grai', text: 'GRAI (Al 8003)' },
  { value: 'giai', text: 'GIAI (Al 8004)' },
  { value: 'gsrn', text: 'GSRN (Al 8018)' },
  { value: 'gsrnp', text: 'GSRNP (Al 8017)' },
  { value: 'gdti', text: 'GDTI (Al 253)' },
  { value: 'gcn', text: 'GCN (Al 255)' },
  { value: 'cpi', text: 'CPI (Al 8010 + Al 8011)' },
  { value: 'ginc', text: 'GINC (Al 401)' },
  { value: 'gsin', text: 'GSIN (Al 402)' },
  { value: 'itip', text: 'ITIP (Al 8006 + Al 21)' },
  { value: 'upui', text: 'UPUI (Al 01 + Al 235)' },
  {
    value: 'Other EPC Identifier',
    text: 'Other EPC Identifier',
    class: 'bold',
    disabled: true
  },
  { value: 'gid', text: 'GID' },
  { value: 'usdod', text: 'USDoD' },
  { value: 'adi', text: 'ADI' },
  { value: 'bic', text: 'BIC' },
  { value: 'imovn', text: 'IMOVN' },
  { value: 'Other', text: 'Other', class: 'bold', disabled: true },
  { value: 'manualURI', text: 'Enter a URI Manually' }
]

exports.ClassIdentifierTypes = [
  { value: null, text: 'Class Identifier Type', disabled: true },
  { value: 'lgtin', text: 'LGTIN (Al 01 + Al 10)' },
  { value: 'gtin', text: 'GTIN, no serial (Al 01)' },
  { value: 'grai', text: 'GRAI, no serial (Al 8003)' },
  { value: 'gdti', text: 'GDTI, no serial (Al 253)' },
  { value: 'gcn', text: 'GCN, no serial (Al 255)' },
  { value: 'cpi', text: 'CPI, no serial (Al 8010)' },
  { value: 'itip', text: 'ITIP, no serial (Al 8006)' },
  { value: 'upui', text: 'UPUI, no TPX (Al 01)' },
  { value: 'Other', text: 'Other', class: 'bold', disabled: true },
  { value: 'manualURI', text: 'Enter a URI Manually' }
]

exports.QuantityType = [
  { value: null, text: 'Select type', class: 'bold' },
  { value: 'Fixed Measure Quantity', text: 'Fixed Measure Quantity' },
  { value: 'Variable Measure Quantity', text: 'Variable Measure Quantity' },
  { value: 'Unspecified Quantity', text: 'Unspecified Quantity' }
]

exports.UOMs = [
  { value: null, text: 'Select UOM', disabled: true },
  { value: 'Mass', text: 'Mass', disabled: true },
  { value: 'KGM', text: 'Kilogram' },
  { value: 'KTN', text: 'Kilotonne' },
  { value: 'LTN', text: 'Ton (UK) or long ton (US)' },
  { value: '2U', text: 'Megagram' },
  { value: 'TNE', text: 'Tonne (metric ton)' },
  { value: 'STN', text: 'Ton (US) or short ton (UK/US)' },
  { value: 'DTN', text: 'Decitonne' },
  { value: 'STI', text: 'Stone (UK)' },
  { value: 'LBR', text: 'Pound' },
  { value: 'HGM', text: 'Hectogram' },
  { value: 'ONZ', text: 'Ounce' },
  { value: 'DJ', text: 'Decagram' },
  { value: 'APZ', text: 'Troy ounce or apothecary ounce' },
  { value: 'GRM', text: 'Gram' },
  { value: 'DG', text: 'Decigram' },
  { value: 'CGM', text: 'Centigram' },
  { value: 'MGM', text: 'Milligram' },
  { value: 'MC', text: 'Microgram' },
  { value: 'F13', text: 'Slug' },
  { value: 'CWI', text: 'Hundred weight (UK)' },
  { value: 'CWA', text: 'Hundred pound (cwt) / hundred weight (US)' },
  { value: 'M86', text: 'Pfund' },
  { value: '58', text: 'Net kilogram' },
  { value: 'DRA', text: 'Dram (US)' },
  { value: 'DRI', text: 'Dram (UK)' },
  { value: 'E4', text: 'Gross kilogram' },
  { value: 'GRN', text: 'Grain' },
  { value: 'PN', text: 'Pound net' },
  { value: 'DWT', text: 'Pennyweight' },
  { value: 'Length', text: 'Length', class: 'bold', disabled: true },
  { value: 'MTR', text: 'Metre' },
  { value: 'A11', text: 'Angstrom' },
  { value: 'A71', text: 'Femtometre' },
  { value: 'C45', text: 'Nanometre' },
  { value: '4H', text: 'Micrometre' },
  { value: 'A12', text: 'Astronomical unit' },
  { value: 'DMT', text: 'Decimetre' },
  { value: 'CMT', text: 'Centimetre' },
  { value: 'MMT', text: 'Millimetre' },
  { value: 'INH', text: 'Inch' },
  { value: 'FOT', text: 'Foot' },
  { value: 'YRD', text: 'Yard' },
  { value: 'NMI', text: 'Nautical mile' },
  { value: 'A45', text: 'Decametre' },
  { value: 'HMT', text: 'Hectometre' },
  { value: 'KMT', text: 'Kilometre' },
  { value: 'B57', text: 'Light year' },
  { value: 'AK', text: 'Fathom' },
  { value: 'M50', text: 'Furlong' },
  { value: 'M49', text: 'Chain (based on US survey foot)' },
  { value: 'X1', text: "Gunter's chain" },
  { value: 'M51', text: 'Foot (US survey)' },
  { value: 'HL', text: 'Hundred foot (linear)' },
  { value: 'LF', text: 'Linear foot' },
  { value: 'LK', text: 'Link' },
  { value: 'LM', text: 'Linear metre' },
  { value: 'SMI', text: 'Mile (statute mile)' }
]
