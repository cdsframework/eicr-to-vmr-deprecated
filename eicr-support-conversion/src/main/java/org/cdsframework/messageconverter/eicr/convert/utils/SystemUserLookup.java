package org.cdsframework.messageconverter.eicr.convert.utils;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HLN Consulting, LLC
 */
public class SystemUserLookup {

    private static Map<String, String>  systemUserLookupMap = new HashMap<>();

    private static final Logger logger = Logger.getLogger(SystemUserLookup.class);

    private static SystemUserLookup systemUserLookup = null;

    private SystemUserLookup() {
        CSVReader csvReader;

        try {

            InputStreamReader isr = new InputStreamReader(this.getClass().getResourceAsStream("/eICR_to_vMR_Payload_Conversion_Specifications_-_Facility_Type_Mapping.csv"));

            csvReader = new CSVReader(isr);

            String[] nextLine;

            boolean isHeader = true;

            while ((nextLine = csvReader.readNext()) != null) {
                if (!isHeader) {
                    systemUserLookupMap.put(nextLine[1], nextLine[4]);

                } else {
                    isHeader = false;

                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("SystemUserLookup csv file cannot be found");

        } catch (IOException e) {
            throw new RuntimeException("SystemUserLookup csv file read error");

        }
    }

    // Returns PROVIDER_FACILITY if it is unmapped
    public static String getVmrSystemUserType(String hl7FacilityCode) {

        if (systemUserLookup==null) {
            systemUserLookup = new SystemUserLookup();

        }

        String systemUserType = systemUserLookupMap.get(hl7FacilityCode);
        if (systemUserType==null || systemUserType.isEmpty()) {
            logger.warn("No cdsSystemUserType mapping for " + hl7FacilityCode + "; defaulting to PROVIDER_FACILITY");
            return CdaConstants.PROVIDER_FACILITY;
        } else {
            logger.debug("HL7 facility code of '" + hl7FacilityCode + "' maps to cdsSystemUserType of '" + systemUserType + "'");
           return systemUserType;
        }

    }

    public static String getEntityType(String systemUserType) {
        if (CdaConstants.PROVIDER_FACILITY.equals(systemUserType)) {
            return CdaConstants.PROVIDER_FACILITY_ABBR;

        } else if (CdaConstants.LAB_FACILITY.equals(systemUserType)) {
            return CdaConstants.LAB_FACILITY_ABBR;

        } else {
            logger.warn("Trying to get EntityType from an unknown SystemUserType: " + systemUserType + ", defaulting to FAC");

        }

        return null;
    }
}
