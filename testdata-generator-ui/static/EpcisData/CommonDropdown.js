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
exports.eventType = [
  { value: null, text: 'Select Event Type', disabled: true },
  { value: 'ObjectEvent', text: 'Object Event' },
  { value: 'AggregationEvent', text: 'Aggregation Event' },
  { value: 'TransactionEvent', text: 'Transaction Event' },
  { value: 'TransformationEvent', text: 'Transformation Event' },
  { value: 'AssociationEvent', text: 'Association Event' }
]

exports.eventType2 = [
  { value: 'ordinaryevent', text: 'Ordinary Event' },
  { value: 'errordeclaration', text: 'Error Declaration' }
]

exports.eventTimeSelector = [
  { value: 'SpecificTime', text: 'Specific Time' },
  { value: 'TimeRange', text: 'Time Range' }
]

exports.companyPrefixs = [
  { value: null, text: 'GCP Length', disabled: true },
  { value: 6, text: '6 Digits' },
  { value: 7, text: '7 Digits' },
  { value: 8, text: '8 Digits' },
  { value: 9, text: '9 Digits' },
  { value: 10, text: '10 Digits' },
  { value: 11, text: '11 Digits' },
  { value: 12, text: '12 Digits' }
]

exports.businessSteps = [
  { value: null, text: 'Select Business Step', disabled: true },
  { value: 'BUSINESSSTEPENTER', text: 'Enter Manually' },
  { value: 'ACCEPTING', text: 'Accepting' },
  { value: 'ARRIVING', text: 'Arriving' },
  { value: 'ASSEMBLING', text: 'Assembling' },
  { value: 'COLLECTING', text: 'Collecting' },
  { value: 'COMMISSIONING', text: 'Commissioning' },
  { value: 'CONSIGNING', text: 'Consigning' },
  { value: 'CREATING_CLASS_INSTANCE', text: 'Creating Class Instance' },
  { value: 'CYCLE_COUNTING', text: 'Cycle Counting' },
  { value: 'DECOMMISSIONING', text: 'Decommissioning' },
  { value: 'DEPARTING', text: 'Departing' },
  { value: 'DESTROYING', text: 'Destroying' },
  { value: 'DISASSEMBLING', text: 'Disassembling' },
  { value: 'DISPENSING', text: 'Dispensing' },
  { value: 'ENCODING', text: 'Encoding' },
  { value: 'ENTERING_EXITING', text: 'Entering Exiting' },
  { value: 'HOLDING', text: 'Holding' },
  { value: 'INSPECTING', text: 'Inspecting' },
  { value: 'INSTALLING', text: 'Installing' },
  { value: 'KILLING', text: 'Killing' },
  { value: 'LOADING', text: 'Loading' },
  { value: 'OTHER', text: 'Other' },
  { value: 'PACKING', text: 'Packing' },
  { value: 'PICKING', text: 'Picking' },
  { value: 'RECEIVING', text: 'Receiving' },
  { value: 'REMOVING', text: 'Removing' },
  { value: 'REPACKAGING', text: 'Repackaging' },
  { value: 'REPAIRING', text: 'Repairing' },
  { value: 'REPLACING', text: 'Replacing' },
  { value: 'RESERVING', text: 'Reserving' },
  { value: 'RETAIL_SELLING', text: 'Retail Selling' },
  { value: 'SAMPLING', text: 'Sampling' },
  { value: 'SENSOR_REPORTING', text: 'Sensor Reporting' },
  { value: 'SHIPPING', text: 'Shipping' },
  { value: 'STAGING_OUTBOUND', text: 'Staging Outbound' },
  { value: 'STOCK_TAKING', text: 'Stock Taking' },
  { value: 'STOCKING', text: 'Stocking' },
  { value: 'STORING', text: 'Storing' },
  { value: 'TRANSPORTING', text: 'Transporting' },
  { value: 'UNLOADING', text: 'Unloading' },
  { value: 'UNPACKING', text: 'Unpacking' },
  { value: 'VOID_SHIPPING', text: 'Void Shipping' }
]

exports.dispositions = [
  { value: null, text: 'Select Disposition', disabled: true },
  { value: 'DISPOSITIONENTER', text: 'Enter Manually' },
  { value: 'ACTIVE', text: 'Active' },
  { value: 'AVAILABLE', text: 'Available' },
  { value: 'COMPLETENESS_VERIFIED', text: 'Completeness Verified' },
  { value: 'COMPLETENESS_INFERRED', text: 'Completeness Inferred' },
  { value: 'CONFORMANT', text: 'Conformant' },
  { value: 'CONTAINER_CLOSED', text: 'Container Closed' },
  { value: 'CONTAINER_OPEN', text: 'Container Open' },
  { value: 'DAMAGED', text: 'Damaged' },
  { value: 'DESTROYED', text: 'Destroyed' },
  { value: 'DISPENSED', text: 'Dispensed' },
  { value: 'DISPOSED', text: 'Disposed' },
  { value: 'ENCODED', text: 'Encoded' },
  { value: 'EXPIRED', text: 'Expired' },
  { value: 'IN_PROGRESS', text: 'In Progress' },
  { value: 'IN_TRANSIT', text: 'In Transit' },
  { value: 'INACTIVE', text: 'Inactive' },
  { value: 'MISMATCH_INSTANCE', text: 'Mismatch Instance' },
  { value: 'MISMATCH_CLASS', text: 'Mismatch Class' },
  { value: 'MISMATCH_QUANTITY', text: 'Mismatch Quantity' },
  { value: 'NEEDS_REPLACEMENT', text: 'Needs Replacement' },
  { value: 'NON_CONFORMANT', text: 'Non Conformant' },
  { value: 'NON_SELLABLE_OTHER', text: 'Non Sellable Other' },
  { value: 'PARTIALLY_DISPENSED', text: 'Partially Dispensed' },
  { value: 'RECALLED', text: 'Recalled' },
  { value: 'RESERVED', text: 'Reserved' },
  { value: 'RETAIL_SOLD', text: 'Retail Sold' },
  { value: 'RETURNED', text: 'Returned' },
  { value: 'SELLABLE_ACCESSIBLE', text: 'Sellable Accessible' },
  { value: 'SELLABLE_NOT_ACCESSIBLE', text: 'Sellable Not Accessible' },
  { value: 'STOLEN', text: 'Stolen' },
  { value: 'UNAVAILABLE', text: 'Uavailable' },
  { value: 'UNKNOWN', text: 'Unknown' }
]

exports.sources = [
  { value: null, text: 'Choose Type' },
  { value: 'OWNING_PARTY', text: 'Owning Party (CBV)' },
  { value: 'PROCESSING_PARTY', text: 'Processing Party (CBV' },
  { value: 'LOCATION', text: 'Location (CBV)' },
  { value: 'OTHER', text: 'Other' }
]

exports.actions = [
  { value: 'ADD', text: 'ADD' },
  { value: 'OBSERVE', text: 'OBSERVE' },
  { value: 'DELETE', text: 'DELETE' }
]

exports.BusinessTransactions = [
  { value: 'bol', text: 'Bill of Lading' },
  { value: 'cert', text: 'Certificate' },
  { value: 'desadv', text: 'Despatch Advice' },
  { value: 'inv', text: 'Invoice' },
  { value: 'pedigree', text: 'Pedigree' },
  { value: 'po', text: 'Purchase Order' },
  { value: 'poc', text: 'Purchase Order Confirmation' },
  { value: 'prodorder', text: 'Production Order' },
  { value: 'recadv', text: 'Receiving Advice' },
  { value: 'rma', text: 'Return Merchandise Authorisation' },
  { value: 'testprd', text: 'Test Procedure' },
  { value: 'testres', text: 'Test Results' },
  { value: 'upevt', text: 'Upstream EPCIS Event' }
]

exports.TimeZones = [
  { value: '00:00', text: 'Greenwich Mean Time (GMT+00:00)' },
  { value: '00:00', text: 'Universal Coordinated Time (GMT+00:00)' },
  { value: '+01:00', text: 'European Central Time (GMT+01:00)' },
  { value: '+02:00', text: 'Eastern European Time (GMT+02:00)' },
  { value: '+03:00', text: 'Eastern African Time (GMT+03:00)' },
  { value: '+03:30', text: 'Middle East Time (GMT+03:30)' },
  { value: '+04:00', text: 'Near East Time (GMT+04:00)' },
  { value: '+05:00', text: 'Pakistan Lahore Time (GMT+5:00)' },
  { value: '+05:30', text: 'India Standard Time (GMT+5:30)' },
  { value: '+06:00', text: 'Bangladesh Standard Time (GMT+6:00)' },
  { value: '+07:00', text: 'Vietnam Standard Time (GMT+7:00)' },
  { value: '+08:00', text: 'China Taiwan Time (GMT+8:00)' },
  { value: '+09:00', text: 'Japan Standard Time (GMT+9:00)' },
  { value: '+09:30', text: 'Australia Central Time (GMT+9:30)' },
  { value: '+10:00', text: 'Australia Eastern Time (GMT+10:00)' },
  { value: '+11:00', text: 'Solomon Standard Time (GMT+11:00)' },
  { value: '+12:00', text: 'New Zealand Standard Time (GMT+12:00)' },
  { value: '-11:00', text: 'Midway Islands Time (GMT-11:00)' },
  { value: '-10:00', text: 'Hawaii Standard Time (GMT-10:00)' },
  { value: '-09:00', text: 'Alaska Standard Time (GMT-9:00)' },
  { value: '-08:00', text: 'Pacific Standard Time (GMT-8:00)' },
  { value: '-07:00', text: 'Phoenix Standard Time (GMT-7:00)' },
  { value: '-07:00', text: 'Mountain Standard Time (GMT-7:00)' },
  { value: '-06:00', text: 'Central Standard Time (GMT-6:00)' },
  { value: '-05:00', text: 'Eastern Standard Time (GMT-5:00)' },
  { value: '-05:00', text: 'Indiana Eastern Standard Time (GMT-5:00)' },
  {
    value: '-04:00',
    text: 'Puerto Rico and US Virgin Islands Time (GMT-4:00)'
  },
  { value: '-03:30', text: 'Canada Newfoundland Time (GMT-3:30)' },
  { value: '-03:00', text: 'Argentina Standard Time (GMT-3:00)' },
  { value: '-03:00', text: 'Brazil Eastern Time (GMT-3:00)' },
  { value: '-01:00', text: 'Central African Time (GMT-1:00)' }
]

exports.ErrorReasons = [
  { value: null, text: 'Choose' },
  { value: 'DID_NOT_OCCUR', text: 'Did Not Occur' },
  { value: 'INCORRECT_DATA', text: 'Incorrect Data' },
  { value: 'OTHER', text: 'Other' }
]

exports.SensorMetaDatas = [
  { value: 'time', text: 'Time' },
  { value: 'startTime', text: 'Start Time' },
  { value: 'endTime', text: 'End Time' },
  { value: 'deviceID', text: 'Device ID' },
  { value: 'deviceMetadata', text: 'Device Metadata' },
  { value: 'rawData', text: 'Raw Data' },
  { value: 'dataProcessingMethod', text: 'Data Processing Method' },
  { value: 'bizRules', text: 'Business Rules' }
]

exports.SensorReportDatas = [
  { value: 'type', text: 'Type' },
  { value: 'deviceID', text: 'Device ID' },
  { value: 'deviceMetadata', text: 'Device Metadata' },
  { value: 'rawData', text: 'Raw Data' },
  { value: 'dataProcessingMethod', text: 'Data Processing Method' },
  { value: 'time', text: 'Time' },
  { value: 'microorganism', text: 'Microorganism' },
  { value: 'chemicalSubstance', text: 'Chemical Substance' },
  { value: 'value', text: 'Value' },
  { value: 'component', text: 'Component' },
  { value: 'stringValue', text: 'String Value' },
  { value: 'booleanValue', text: 'Boolean Value' },
  { value: 'hexBinaryValue', text: 'Hex Binary Value' },
  { value: 'uriValue', text: 'URI Value' },
  { value: 'minValue', text: 'Min Value' },
  { value: 'maxValue', text: 'Max Value' },
  { value: 'meanValue', text: 'Mean Value' },
  { value: 'sDev', text: 'Standard Deviation' },
  { value: 'percRank', text: 'Perc Rank' },
  { value: 'percValue', text: 'Perc Value' },
  { value: 'uom', text: 'UOM' }
]

exports.sensorReportTypes = [
  { value: null, text: 'Select UOM', disabled: true },
  { value: 'User_Defined_Sensor', text: 'User defined sensor' },
  { value: 'Absorbed_dose', text: 'Absorbed dose' },
  { value: 'Absorbed_dose_rate', text: 'Absorbed dose rate' },
  { value: 'Acceleration', text: 'Acceleration' },
  { value: 'Altitude_Elevation', text: 'Altitude/Elevation' },
  { value: 'Amount_of_substance', text: 'Amount of substance' },
  { value: 'Angle', text: 'Angle' },
  { value: 'Angular_impulse', text: 'Angular impulse' },
  { value: 'Angular_momentum', text: 'Angular momentum' },
  { value: 'Area', text: 'Area' },
  { value: 'Capacitance', text: 'Capacitance' },
  { value: 'Charge', text: 'Charge' },
  { value: 'Conductance', text: 'Conductance' },
  { value: 'Conductivity', text: 'Conductivity' },
  { value: 'Count', text: 'Count' },
  { value: 'Current', text: 'Current' },
  { value: 'Current desity', text: 'Current density' },
  { value: 'Density', text: 'Density' },
  { value: 'Dimensionless_concentration', text: 'Dimensionless concentration' },
  { value: 'Dynamic_viscosity', text: 'Dynamic viscosity' },
  { value: 'Effective_dose', text: 'Effective dose' },
  { value: 'Effective_dose_rate', text: 'Effective dose rate' },
  { value: 'Electric_field_intensity', text: 'Electric field intensity' },
  { value: 'Energy', text: 'Energy' },
  { value: 'Exposure', text: 'Exposure' },
  { value: 'Force', text: 'Force' },
  { value: 'Frequency', text: 'Frequency' },
  { value: 'Humidity', text: 'Humidity' },
  { value: 'Illuminance', text: 'Illuminance' },
  { value: 'Impulse', text: 'Impulse/Linear momentum' },
  { value: 'Inductance', text: 'Inductance' },
  { value: 'Irradiance', text: 'Irradiance' },
  { value: 'Kinematic_viscosity', text: 'Kinematic viscosity' },
  { value: 'Latitude', text: 'Latitude' },
  { value: 'Length', text: 'Length' },
  { value: 'Longitude', text: 'Longitude' },
  { value: 'Luminous_flux', text: 'Luminous flux' },
  { value: 'Luminous_intensity', text: 'Luminous intensity' },
  { value: 'Magnetic_flux', text: 'Magnetic flux' },
  { value: 'Magnetic_flux_density', text: 'Magnetic flux density' },
  { value: 'Magnetic_vector_potential', text: 'Magnetic vector potential' },
  { value: 'Mass', text: 'Mass' },
  { value: 'Mass_flow_rate', text: 'Mass flow rate' },
  { value: 'Mass_flux', text: 'Mass flux' },
  { value: 'Memory_capacity', text: 'Memory capacity' },
  { value: 'Molar_concentration', text: 'Molar concentration' },
  { value: 'Molar_mass', text: 'Molar mass' },
  { value: 'Molar_thermodynamic_energy', text: 'Molar thermodynamic energy' },
  { value: 'Molar_volume', text: 'Molar volume' },
  { value: 'Power', text: 'Power' },
  { value: 'Pressure', text: 'Pressure' },
  { value: 'Radiant_flux', text: 'Radiant flux' },
  { value: 'Radiant_intensity', text: 'Radiant intensity' },
  { value: 'Radiant_intensity', text: 'Radiant_intensity' },
  { value: 'Resistance', text: 'Resistance' },
  { value: 'Resistivity', text: 'Resistivity' },
  { value: 'Specific_volume', text: 'Specific_volume' },
  { value: 'Speed', text: 'Speed' },
  { value: 'Velocity', text: 'Velocity' },
  { value: 'Temperature', text: 'Temperature' },
  { value: 'Time', text: 'Time' },
  { value: 'Torque', text: 'Torque' },
  { value: 'Voltage', text: 'Voltage' },
  { value: 'Volume', text: 'Volume' },
  { value: 'Volumetric_flow_rate', text: 'Volumetric flow rate' },
  { value: 'Volumetric_flux', text: 'Volumetric flux' }
]

exports.sensorReportUOMs = [
  { value: 'gray', text: 'Gray', UOMtype: 'Absorbed_dose' },
  {
    value: 'gray per second',
    text: 'Gray per second',
    UOMtype: 'Absorbed_dose_rate'
  },
  {
    value: 'meter per second',
    text: 'Meter per second',
    UOMtype: 'Acceleration'
  },
  { value: 'per second', text: 'Per second', UOMtype: 'Acceleration' },
  { value: 'metres', text: 'Metres', UOMtype: 'Altitude_Elevation' },
  { value: 'mole', text: 'Mole', UOMtype: 'Amount_of_substance' },
  { value: 'degree', text: 'Degree', UOMtype: 'Angle' },
  { value: 'radians', text: 'radians', UOMtype: 'Angle' },
  {
    value: 'newton metre second',
    text: 'Newton metre second',
    UOMtype: 'Angular_impulse'
  },
  {
    value: 'kilogram metre squared',
    text: 'Kilogram metre squared',
    UOMtype: 'Angular_impulse'
  },
  { value: 'per second', text: 'Per second', UOMtype: 'Angular_impulse' },
  {
    value: 'newton metre second',
    text: 'Newton metre second',
    UOMtype: 'Angular_momentum'
  },
  {
    value: 'kilogram metre squared',
    text: 'Kilogram metre squared',
    UOMtype: 'Angular_momentum'
  },
  { value: 'per second', text: 'Per second', UOMtype: 'Angular_momentum' },
  { value: 'square metre', text: 'Square metre', UOMtype: 'Area' },
  { value: 'farad', text: 'Farad', UOMtype: 'Capacitance' },
  { value: 'coulomb', text: 'Coulomb', UOMtype: 'Charge' },
  { value: 'siemen', text: 'Siemen', UOMtype: 'Conductance' },
  { value: 'siemen per metre', text: 'Siemen per metre', UOMtype: 'Count' },
  { value: 'ampere', text: 'Ampere', UOMtype: 'Current' },
  {
    value: 'ampere per square metre',
    text: 'Ampere per square metre',
    UOMtype: 'Current density'
  },
  {
    value: 'Kilogram per cubic metre',
    text: 'Kilogram per cubic metre',
    UOMtype: 'Density'
  },
  {
    value: 'parts per million',
    text: 'Parts per million',
    UOMtype: 'Dimensionless_concentration'
  },
  { value: 'pascal', text: 'Pascal', UOMtype: 'Dynamic_viscosity' },
  { value: 'sievert', text: 'Sievert', UOMtype: 'Effective_dose' },
  {
    value: 'sievert per second',
    text: 'Sievert per second',
    UOMtype: 'Effective_dose_rate'
  },
  {
    value: 'volt per metre',
    text: 'Volt per metre',
    UOMtype: 'Electric_field_intensity'
  },
  { value: 'joule', text: 'Joule', UOMtype: 'Energy' },
  { value: 'lux second', text: 'Lux second', UOMtype: 'Exposure' },
  { value: 'newton', text: 'Newton', UOMtype: 'Force' },
  { value: 'hertz', text: 'Hertz', UOMtype: 'Frequency' },
  { value: 'KMQ', text: 'Kilogram per cubic metre', UOMtype: 'Humidity' },
  { value: '23', text: 'Gram per cubic centimetre', UOMtype: 'Humidity' },
  { value: 'D41', text: 'Tonne per cubic metre', UOMtype: 'Humidity' },
  { value: 'GJ', text: 'Gram per millilitre', UOMtype: 'Humidity' },
  { value: 'B35', text: 'Kilogram per litre', UOMtype: 'Humidity' },
  { value: 'GL', text: 'Gram per litre', UOMtype: 'Humidity' },
  { value: 'A93', text: 'Gram per cubic metre', UOMtype: 'Humidity' },
  { value: 'GP', text: 'Milligram per cubic metre', UOMtype: 'Humidity' },
  { value: 'B72', text: 'Megagram per cubic metre', UOMtype: 'Humidity' },
  { value: 'B34', text: 'Kilogram per cubic decimetre', UOMtype: 'Humidity' },
  { value: 'H29', text: 'Microgram per litre', UOMtype: 'Humidity' },
  { value: 'M1', text: 'Milligram per litre', UOMtype: 'Humidity' },
  { value: 'GQ', text: 'Microgram per cubic metre', UOMtype: 'Humidity' },
  { value: 'F23', text: 'Gram per cubic decimetre', UOMtype: 'Humidity' },
  { value: 'G31', text: 'Kilogram per cubic centimetre', UOMtype: 'Humidity' },
  { value: '87', text: 'Pound per cubic foot', UOMtype: 'Humidity' },
  { value: 'GE', text: 'Pound per gallon (US)', UOMtype: 'Humidity' },
  { value: 'LA', text: 'Pound per cubic inch', UOMtype: 'Humidity' },
  { value: 'G32', text: 'Ounce per cubic yard', UOMtype: 'Humidity' },
  { value: 'K41', text: 'Grain per gallon (US)', UOMtype: 'Humidity' },
  { value: 'K71', text: 'Pound per gallon (UK)', UOMtype: 'Humidity' },
  { value: 'K84', text: 'Pound per cubic yard', UOMtype: 'Humidity' },
  { value: 'L37', text: 'Ounce per gallon (UK)', UOMtype: 'Humidity' },
  { value: 'L38', text: 'Ounce per gallon (US)', UOMtype: 'Humidity' },
  { value: 'L39', text: 'Ounce per cubic inch', UOMtype: 'Humidity' },
  { value: 'L65', text: 'Slug per cubic foot', UOMtype: 'Humidity' },
  { value: 'L92', text: 'Ton (UK long) per cubic yard', UOMtype: 'Humidity' },
  { value: 'L93', text: 'Ton (US short) per cubic yard', UOMtype: 'Humidity' },
  { value: 'B60', text: 'lumen per square metre', UOMtype: 'Illuminance' },
  { value: 'LUX', text: 'lux', UOMtype: 'Illuminance' },
  { value: 'KLX', text: 'kilolux', UOMtype: 'Illuminance' },
  { value: 'P25', text: 'lumen per square foot', UOMtype: 'Illuminance' },
  { value: 'P26', text: 'phot', UOMtype: 'Illuminance' },
  { value: 'P27', text: 'footcandle', UOMtype: 'Illuminance' },
  { value: 'newton seconds', text: 'Newton seconds', UOMtype: 'Impulse' },
  { value: 'henry', text: 'Henry', UOMtype: 'Inductance' },
  {
    value: 'watt_per_square_metre',
    text: 'Watt per square metre',
    UOMtype: 'Irradiance'
  },
  {
    value: 'square_metres_per_second',
    text: 'Square metres per second',
    UOMtype: 'Kinematic_viscosity'
  },
  { value: 'degrees', text: 'Degrees', UOMtype: 'Latitude' },
  { value: 'metre', text: 'Metre', UOMtype: 'Length' },
  { value: 'degrees', text: 'Degrees', UOMtype: 'Longitude' },
  { value: 'lumen', text: 'Lumen', UOMtype: 'Luminous_flux' },
  { value: 'candela', text: 'Candela', UOMtype: 'Luminous_intensity' },
  { value: 'weber', text: 'Weber', UOMtype: 'Magnetic_flux' },
  { value: 'tesla', text: 'Tesla', UOMtype: 'Magnetic_flux_density' },
  {
    value: 'weber_per_metre',
    text: 'Weber per metre',
    UOMtype: 'Magnetic_vector_potential'
  },
  {
    value: 'joules_per_ampere_metre',
    text: 'Joules per ampere metre',
    UOMtype: 'Magnetic_vector_potential'
  },
  { value: 'kilogram', text: 'Kilogram', UOMtype: 'Mass' },
  {
    value: 'kilogram_per_second',
    text: 'Kilogram per second',
    UOMtype: 'Mass_flow_rate'
  },
  {
    value: 'kilogram_per_second_per_square_metre',
    text: 'Kilogram per second per square metre',
    UOMtype: 'Mass_flux'
  },
  { value: 'byte', text: 'Byte', UOMtype: 'Memory_capacity' },
  {
    value: 'mole_per_cubic_metre',
    text: 'Mole per cubic metre',
    UOMtype: 'Molar_concentration'
  },
  {
    value: 'kilogram_per_mole',
    text: 'Kilogram per mole',
    UOMtype: 'Molar_mass'
  },
  {
    value: 'joule_per_mole',
    text: 'Joule per mole',
    UOMtype: 'Molar_thermodynamic_energy'
  },
  {
    value: 'cubic_metre_per_mole',
    text: 'Cubic metre per mole',
    UOMtype: 'Molar_volume'
  },
  { value: 'watt', text: 'Watt', UOMtype: 'Power' },
  { value: 'pascal', text: 'Pascal', UOMtype: 'Pressure' },
  {
    value: 'Newton_per_square_meter',
    text: 'Newton per square meter',
    UOMtype: 'Pressure'
  },
  { value: 'watt', text: 'Watt', UOMtype: 'Radiant_flux' },
  { value: 'watt', text: 'Watt', UOMtype: 'Radiant_intensity' },
  { value: 'steradian', text: 'Steradian', UOMtype: 'Radiant_intensity' },
  { value: 'becquerel', text: 'Becquerel', UOMtype: 'Radioactivity' },
  { value: 'ohm', text: 'Ohm', UOMtype: 'becquerel' },
  { value: 'ohm metre', text: 'Ohm metre', UOMtype: 'Resistivity' },
  {
    value: 'cubic_metres_per_kilogram',
    text: 'Cubic metres per kilogram',
    UOMtype: 'Specific_volume'
  },
  { value: 'metre_per_second', text: 'Metre per second', UOMtype: 'Speed' },
  { value: 'metre_per :econd', text: 'Metre per second', UOMtype: 'Velocity' },
  { value: 'KEL', text: 'Kelvin', UOMtype: 'Temperature' },
  { value: 'FAH', text: 'Fahrenheit', UOMtype: 'Temperature' },
  { value: 'CEL', text: 'Celsius', UOMtype: 'Temperature' },
  { value: 'A48', text: 'Rankine', UOMtype: 'Temperature' },
  { value: 'second', text: 'Second', UOMtype: 'Time' },
  { value: 'newton metre', text: 'Newton metre', UOMtype: 'Torque' },
  { value: 'volt', text: 'Volt', UOMtype: 'Voltage' },
  { value: 'cubic metre', text: 'Cubic metre', UOMtype: 'Volume' },
  {
    value: 'cubic_metre_per_second',
    text: 'Cubic metre per second',
    UOMtype: 'Volumetric_flow_rate'
  },
  {
    value: 'cubic_metre_per_second_per_square_metre',
    text: 'Cubic metre per second per square metre',
    UOMtype: 'Volumetric_flux'
  }
]

exports.hashAlgorithmTypes = [
  { value: null, text: 'Hash Algorithm Type', disabled: true },
  { value: 'sha-1', text: 'SHA-1' },
  { value: 'sha-224', text: 'SHA-224' },
  { value: 'sha-256', text: 'SHA-256' },
  { value: 'sha-384', text: 'SHA-384' },
  { value: 'sha-512', text: 'SHA-512' },
  { value: 'sha3-224', text: 'SHA3-224' },
  { value: 'sha3-256', text: 'SHA3-256' },
  { value: 'sha3-512', text: 'SHA3-512' },
  { value: 'md2', text: 'MD-2' },
  { value: 'md5', text: 'MD-5' }
]
