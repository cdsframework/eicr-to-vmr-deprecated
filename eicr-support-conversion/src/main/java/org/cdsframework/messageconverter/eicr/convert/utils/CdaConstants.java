/**
 * Copyright (c) 2014, The Board of Trustees of the University of Illinois
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.cdsframework.messageconverter.eicr.convert.utils;

/**
 *
 * copied from cda-support-conversion
 *
 * @author sdn
 */
public class CdaConstants {
    public static final String LOINC_CODE_SYSTEM_OID = "2.16.840.1.113883.6.1";
    public static final String LOINC_CODE_SYSTEM_NAME = "LOINC";
    public static final String CONSENT_SECTION_TEMPLATE_ID = "2.16.840.1.113883.3.445.17";
    public static final String CONSENT_SECTION_TEMPLATE_NAME = "Consent Directive Details Section";
    public static final String CONSENT_SECTION_TEMPLATE_TITLE = "Consent Directive Details";
    public static final String HL7_ACT_CLASS_CODE_OID = "2.16.840.1.113883.5.6";

    public static final String FACILITY_ENTITY_TYPE_CODE_SYSTEM = "2.16.840.1.113883.3.795.5.4.12.2.1";

    public static final String RELATEDENTITY_FACILITY_ID_ROOT = "2.1.1.3";
    public static final String RELATEDENTITY_FACILITY_TEMPLATEID_ROOT = "2.16.840.1.113883.3.1829.11.16.2.20";

    // encounter observation type code
    public static final String DIAGNOSIS = "29308-4";

    // system user types
    public static final String PROVIDER_FACILITY = "PROVIDER_FACILITY";
    public static final String PROVIDER_FACILITY_ABBR = "FAC";
    public static final String LAB_FACILITY = "LAB_FACILITY";
    public static final String LAB_FACILITY_ABBR = "LAB";
    public static final String SYSTEMUSERTYPE_CODESYSTEM = "2.16.840.1.113883.3.795.5.4.12.2.1";

    /////// public static final String PROBLEM_STATUS_CODE_SYSTEM = "2.16.840.1.113883.3.88.12.80.68";
    public static final String PROBLEM_STATUS_CODE_SYSTEM = "2.16.840.1.113883.3.1937.98.5.8";
    public static final String CDSINPUT_TEMPLATE_ID = "2.16.840.1.113883.3.1829.11.1.1.8";
    public static final String ENCOUNTER_TEMPLATE_ID = "2.16.840.1.113883.3.1829.11.4.3.8";

    public static final String PROBLEM_DETAIL_DIAGNOSIS_TEMPLATE_ID = "2.16.840.1.113883.3.1829.11.7.2.20";
    public static final String PATIENT_DETAIL_TEMPLATE_ID = "2.16.840.1.113883.3.1829.11.2.3.3";
    public static final String GENDER_CODE_SYSTEM = "2.16.840.1.113883.1.11.1";
    public static final String LAB_RESULTS_TEMPLATE_ID = "2.16.840.1.113883.3.1829.11.6.3.15";
    public static final String SUBSTANCE_ADMIN_TEMPLATE_ID = "2.16.840.1.113883.3.1829.11.9.1.14";
    public static final String SUBSTANCE_ADMIN_GENERALPURPOSE_TEMPLATE_ID = "2.16.840.1.113883.6.96";
    public static final String SUBSTANCE_CODE_TEMPLATE_ID = "2.16.840.1.113883.3.1829.11.13.6.2";
    public static final String VMR_TEMPLATE_ID = "2.16.840.1.113883.3.1829.11.1.2.18";
}
