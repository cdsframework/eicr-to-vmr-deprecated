package org.cdsframework.messageconverter.eicr.convert.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HLN Consulting, LLC
 */
public class ProblemTypeCodeLookup {
    
    private static Map<String, ProblemType> problemTypeMap = new HashMap();

    private static String [][] data =
    {
         {"75326-9", "Problem HL7.CCDAR2",                              "LOINC", "2.16.840.1.113883.6.1", "General Problem",         "2.16.840.1.113883.3.1829.11.7.2.21"},
         {"75325-1", "Symptom HL7.CCDAR2",                              "LOINC", "2.16.840.1.113883.6.1", "General Problem",         "2.16.840.1.113883.3.1829.11.7.2.21"},
         {"75324-4", "Diagnosis",                                       "LOINC", "2.16.840.1.113883.6.1", "General Problem",         "2.16.840.1.113883.3.1829.11.7.2.21"},
         {"75321-0", "Clinical finding HL7.CCDAR2",                     "LOINC", "2.16.840.1.113883.6.1", "General Problem",         "2.16.840.1.113883.3.1829.11.7.2.21"},
         {"75323-6", "Condition HL7.CCDAR2",                            "LOINC", "2.16.840.1.113883.6.1", "General Problem",         "2.16.840.1.113883.3.1829.11.7.2.21"},
         {"29308-4", "Complaint HL7.CCDAR2",                            "LOINC", "2.16.840.1.113883.6.1", "General Problem",         "2.16.840.1.113883.3.1829.11.7.2.21"},
         {"75322-8", "Functional performance HL7.CCDAR2",               "LOINC", "2.16.840.1.113883.6.1", "General Problem",         "2.16.840.1.113883.3.1829.11.7.2.21"},
         {"75275-8", "Cognitive Function HL7.CCDAR2",                   "LOINC", "2.16.840.1.113883.6.1", "General Problem",         "2.16.840.1.113883.3.1829.11.7.2.21"},
         {"75318-6", "Problem family member HL7.CCDAR2",                "LOINC", "2.16.840.1.113883.6.1", "General Problem",         "2.16.840.1.113883.3.1829.11.7.2.21"},
         {"75319-4", "Symptom family member HL7.CCDAR2",                "LOINC", "2.16.840.1.113883.6.1", "General Problem",         "2.16.840.1.113883.3.1829.11.7.2.21"},
         {"75317-8", "Diagnosis family member HL7.CCDAR2",              "LOINC", "2.16.840.1.113883.6.1", "General Problem",         "2.16.840.1.113883.3.1829.11.7.2.21"},
         {"75316-0", "Clinical finding family member HL7.CCDAR2",       "LOINC", "2.16.840.1.113883.6.1", "General Problem",         "2.16.840.1.113883.3.1829.11.7.2.21"},
         {"75315-2", "Condition family member HL7.CCDAR2",              "LOINC", "2.16.840.1.113883.6.1", "General Problem",         "2.16.840.1.113883.3.1829.11.7.2.21"},
         {"75314-5", "Complaint family member HL7.CCDAR2",              "LOINC", "2.16.840.1.113883.6.1", "General Problem",         "2.16.840.1.113883.3.1829.11.7.2.21"},
         {"75313-7", "Functional performance family member HL7.CCDR2",  "LOINC", "2.16.840.1.113883.6.1", "General Problem",         "2.16.840.1.113883.3.1829.11.7.2.21"},
         {"75312-9", "Cognitive function family member HL7.CCDAR2",     "LOINC", "2.16.840.1.113883.6.1", "General Problem",         "2.16.840.1.113883.3.1829.11.7.2.21"}
     };

    static {
        for (String [] problemTypeArr : data) {
            ProblemType problemType = new ProblemType(problemTypeArr[0], problemTypeArr[1], problemTypeArr[2], problemTypeArr[3], problemTypeArr[4], problemTypeArr[5]);
            problemTypeMap.put(problemType.getCode(), problemType);

        }

    }

    public static String getOID(String code) {
        ProblemType problemType = problemTypeMap.get(code);

        if (problemType==null) {
            return null;

        }

        return problemType.getResultingOID();

    }
}
