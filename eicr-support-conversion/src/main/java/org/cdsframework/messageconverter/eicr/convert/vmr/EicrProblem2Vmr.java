package org.cdsframework.messageconverter.eicr.convert.vmr;

import org.cdsframework.cds.vmr.CdsInputWrapper;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.messageconverter.eicr.convert.exception.EicrException;
import org.cdsframework.messageconverter.eicr.convert.utils.CdaConstants;
import org.cdsframework.messageconverter.eicr.convert.utils.CodeFinder;
import org.cdsframework.messageconverter.eicr.convert.utils.IdentifierUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.IntervalUtils;
import org.cdsframework.messageconverter.eicr.convert.utils.TemplateIdSetter;
import org.eclipse.emf.common.util.EList;
import org.opencds.vmr.v1_0.schema.Problem;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.openhealthtools.mdht.uml.cda.Entry;
import org.openhealthtools.mdht.uml.cda.Observation;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;

import java.util.Iterator;
import java.util.List;
import org.cdsframework.cds.util.CdsObjectFactory;

import static org.cdsframework.messageconverter.eicr.convert.utils.CdaConstants.DIAGNOSIS;
import org.cdsframework.util.LogUtils;

/**
 * @author HLN Consulting, LLC
 */
public class EicrProblem2Vmr {

    private static final LogUtils logger = LogUtils.getLogger(EicrProblem2Vmr.class);

    public static void addProblems(EList<Entry> entries, CdsInputWrapper input) throws EicrException {

        logger.debug("starting addProblems()...");

        for (Entry entry : entries) {
            try {
                EicrAct2Vmr.addActAsProblem(entry.getAct(), input);
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    public static void addProblemAsRelatedClinicalStatement(String idRoot, Observation observation, List<RelatedClinicalStatement> relatedClinicalStatements) throws EicrException {

        if (observation != null) {

            String[] idElements = IdentifierUtils.getIdElements(observation.getIds(), idRoot);

            String statusCode = observation.getStatusCode().getCode();
            String statusCodeSystem = CodeFinder.getCodeSystem(CodeFinder.CodeSystem.PROBLEMSTATUS_CODESYSTEM);
            String statusCodeSystemName = CodeFinder.getCodeSystemName(CodeFinder.CodeSystem.PROBLEMSTATUS_CODESYSTEM);
            String statusDisplayName = observation.getStatusCode().getDisplayName();

            String[] eventTimeElements = IntervalUtils.getEventTimeElements(observation.getEffectiveTime());

            RelatedClinicalStatement result = CdsObjectFactory.getRelatedClinicalStatement("PERT");
            relatedClinicalStatements.add(result);

            Iterator it = observation.getValues().iterator();
            CD problemCodes = null;
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj instanceof CD) {
                    problemCodes = (CD) obj;

                }
            }

            String problemCodeCode = problemCodes != null ? problemCodes.getCode() : null;
            String problemCodeCodeSystem = problemCodes != null ? problemCodes.getCodeSystem() : null;
            String problemCodeCodeSystemName = problemCodes != null ? problemCodes.getCodeSystemName() : null;
            String problemCodeCodeDisplayName = problemCodes != null ? problemCodes.getDisplayName() : null;

            Problem problem;
            try {
                problem = CdsObjectFactory.getProblem(
                        problemCodeCode,
                        problemCodeCodeDisplayName,
                        problemCodeCodeSystem,
                        problemCodeCodeSystemName,
                        statusCode,
                        statusDisplayName,
                        statusCodeSystem,
                        statusCodeSystemName,
                        eventTimeElements[0],
                        eventTimeElements[1]);

                TemplateIdSetter.setTemplateId(problem.getTemplateId(), CdaConstants.PROBLEM_DETAIL_DIAGNOSIS_TEMPLATE_ID);

            } catch (CdsException e) {
                throw new EicrException("Error adding 'Problem' to CdsInput message", e);

            }

            if (DIAGNOSIS.equals(observation.getCode().getCode())) {
                problem.setDiagnosticEventTime(IntervalUtils.convertTime(observation.getEffectiveTime()));

            }

            problem.setId(IdentifierUtils.getII(idElements));
            result.setProblem(problem);

        }
    }
}
