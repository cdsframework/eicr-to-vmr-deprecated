package org.cdsframework.messageconverter;

import org.cdsframework.messageconverter.eicr.convert.utils.SystemUserLookup;
import org.junit.Test;

/**
 * Unit test for simple Eicr2Vmr.
 */
public class Eicr2VmrTest {

    @Test
    public void testSystemUserLookup() {

        String result = SystemUserLookup.getVmrSystemUserType("EPIL");

        System.out.println(result);
    }
}
