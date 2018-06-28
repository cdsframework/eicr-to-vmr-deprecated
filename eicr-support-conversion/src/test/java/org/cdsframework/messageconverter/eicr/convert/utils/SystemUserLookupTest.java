package org.cdsframework.messageconverter.eicr.convert.utils;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * @author HLN Consulting, LLC
 */
public class SystemUserLookupTest {

    @Test
    public void testGetVmrSystemUserType_found() throws Exception {
        ClassLoader.getSystemClassLoader().loadClass("org.cdsframework.messageconverter.eicr.convert.utils.SystemUserLookup");
        assertEquals("LAB_FACILITY", SystemUserLookup.getVmrSystemUserType("GIDX"));

    }
//
//    @Test
//    public void testGetVmrSystemUserType_not_found_and_defaulted() throws Exception {
//        ClassLoader.getSystemClassLoader().loadClass("org.cdsframework.messageconverter.eicr.convert.utils.SystemUserLookup");
//        System.out.println("SystemUserLookup.getVmrSystemUserType(\"SSSAAAAA\")=" + SystemUserLookup.getVmrSystemUserType("SSSAAAAA"));
//        assertEquals("PROVIDER_FACILITY", SystemUserLookup.getVmrSystemUserType("SSSAAAAA"));
//
//    }
}