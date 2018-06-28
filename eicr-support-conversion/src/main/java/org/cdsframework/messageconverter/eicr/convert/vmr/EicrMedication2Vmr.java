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
package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.cdsframework.cds.vmr.CdsInputWrapper;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.openhealthtools.mdht.uml.cda.Entry;

import java.util.List;
import org.cdsframework.util.LogUtils;

/**
 *
 * @author sdn
 */
public class EicrMedication2Vmr {

    private final static LogUtils logger = LogUtils.getLogger(EicrMedication2Vmr.class);

    /**
     * Adds Consolidated CDA Medication entries as vMR
     * SubstanceAdministrationEvents.
     *
     * @param entries
     * @param input
     * @throws EicrException
     */
    public static void addMedications(List<Entry> entries, CdsInputWrapper input)
            throws EicrException {

        logger.debug("starting addMedications()...");

        for (Entry entry : entries) {
            if (entry != null) {
                if (entry.getSubstanceAdministration() != null) {
                    try {
                        EicrSubstanceAdministration2Vmr.addSubstanceAdministrationAsSubstanceAdministrationEvent(entry.getSubstanceAdministration(), input);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                } else {
                    logger.error("entry is null!");

                }
            } else {
                logger.error("entry is null!");

            }
        }
    }
}
