package org.cdsframework.messageconverter;

import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.cdsframework.cds.vmr.CdsObjectAssist;
import org.junit.Ignore;
import org.junit.Test;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit test for simple EICRTovMRConverter.
 */
public class EICRTovMRConverterTest {

    private XPathFactory xpathFactory = XPathFactory.newInstance();

    private static final Logger logger = Logger.getLogger(EICRTovMRConverterTest.class);

    public static Map<String, String> addressParts = new HashMap<>();

    static {
        addressParts.put("SAL", "streetAddressLine");
        addressParts.put("CTY", "city");
        addressParts.put("STA", "state");
        addressParts.put("ZIP", "postalCode");
        addressParts.put("CNT", "country");

    }

    /**
     * Test of getCdsInputFromClinicalDocument method of class Eicr2Vmr for 'Record Target' section.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDemographicsSection() throws Exception, Throwable {

        ClinicalDocument clinicalDocument = CDAUtil.load(new FileInputStream("src/test/resources/samples/good_messages/eICR_DemographicsSection.xml"));
        CDSInput result = Eicr2Vmr.getCdsInputFromClinicalDocument(clinicalDocument);

        String vmrInputStr = CdsObjectAssist.cdsObjectToString(result, CDSInput.class);
        save(vmrInputStr, "src/test/resources/samples/good_messages/eICR_DemographicsSection_cdsInput.xml");

        String eicrStr = new String(Files.readAllBytes(Paths.get("src/test/resources/samples/good_messages/eICR_DemographicsSection.xml")));

        Document vmrInputDocument = getDocument(vmrInputStr);
        Document eicrDocument = getDocument(eicrStr);

        XPathExpression expr = xpathFactory.newXPath().compile("//demographics");

        Object vmrInputResult = expr.evaluate(vmrInputDocument, XPathConstants.NODESET);

        NodeList nodes = ((NodeList) vmrInputResult).item(0).getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            if ("birthTime".equals(nodes.item(i).getNodeName())) {
                logger.info("Comparing vmr:demographics/birthTime with eicr:patientRole/patient/birthTime");
                assertEquals(getDateOfBirth(nodes, i), getElementAttribute(eicrDocument, "birthTime", "value"));

            }

            if ("gender".equals(nodes.item(i).getNodeName())) {
                logger.info("Comparing vmr:demographics/gender with eicr:patientRole/patient/administrativeGenderCode");
                assertEquals(getGender(nodes, i), getElementAttribute(eicrDocument, "administrativeGenderCode", "code"));

            }

            if ("address".equals(nodes.item(i).getNodeName())) {
                logger.info("Comparing vmr:demographics/address with eicr:patientRole/addr");
                compareAddresses(nodes.item(i), getElement(eicrDocument, "patientRole/addr"));

            }
        }
    }

    @Test
    public void testAddHealthCareFacilityAddress2Vmr() throws Exception, Throwable {
        ClinicalDocument clinicalDocument = CDAUtil.load(new FileInputStream("src/test/resources/samples/good_messages//eICR_HealthCareFacility.xml"));
        CDSInput result = Eicr2Vmr.getCdsInputFromClinicalDocument(clinicalDocument);

        String vmrInputStr = CdsObjectAssist.cdsObjectToString(result, CDSInput.class);
        save(vmrInputStr, "src/test/resources/samples/good_messages/eICR_HealthCareFacility_cdsInput.xml");

        String eicrStr = new String(Files.readAllBytes(Paths.get("src/test/resources/samples/good_messages/eICR_HealthCareFacility.xml")));

        Document vmrInputDocument = getDocument(vmrInputStr);
        Document eicrDocument = getDocument(eicrStr);

        XPathExpression expr = xpathFactory.newXPath().compile("//relatedEntity");

        Object vmrInputResult = expr.evaluate(vmrInputDocument, XPathConstants.NODESET);

        NodeList nodes = (NodeList) vmrInputResult;

        // relatedEntities loop
        for (int j=0; j<nodes.getLength(); j++) {
            Node jnode = nodes.item(j);

            // loop around relatedEntity elements
            for (int k=0; k<jnode.getChildNodes().getLength(); k++) {
                Node knode = jnode.getChildNodes().item(k);

                if ("facility".equals(knode.getNodeName())) {

                    // loop around facility elements
                    for (int i = 0; i < knode.getChildNodes().getLength(); i++) {
                        if ("address".equals(knode.getChildNodes().item(i).getNodeName())) {
                            logger.info("Comparing vmr:patient/relatedEntity/facility/address* with eicr:componentOf/encompassingEncounter/location/healthCareFacility/location/addr*");
                            compareAddresses(knode.getChildNodes().item(i), getElement(eicrDocument, "healthCareFacility/location/addr"));

                        }
                    }
                }
            }
        }
    }

    /**
     * eicr://encounter/id/@root                vmr://encounterEvent/id@root
     * eicr://encounter/code/@code              vmr://encounterEvent/encounterType/@code
     * eicr://encounter/effectiveTime/@value    vmr://encounterEvent/encounterEventTime/@low
     * eicr://observation/id/@root              vmr://observationResult/id/@root
     * eicr://observation                       vmr://observationResult
     * @throws Exception
     */
    @Test
    public void testEncounters2Vmr() throws Exception, Throwable {
        ClinicalDocument clinicalDocument = CDAUtil.load(new FileInputStream("src/test/resources/samples/good_messages/eICR_EncountersSection_with_problem.xml"));
        CDSInput result = Eicr2Vmr.getCdsInputFromClinicalDocument(clinicalDocument);

        String vmrInputStr = CdsObjectAssist.cdsObjectToString(result, CDSInput.class);
        save(vmrInputStr, "src/test/resources/samples/good_messages/eICR_EncountersSection_with_problem_cdsInput.xml");

        String eicrStr = new String(Files.readAllBytes(Paths.get("src/test/resources/samples/good_messages/eICR_EncountersSection_with_problem.xml")));

        Document vmrInputDocument = getDocument(vmrInputStr);
        Document eicrDocument = getDocument(eicrStr);

        DTMNodeList eicrElementList = getElement(eicrDocument, "ClinicalDocument/component/structuredBody/component/section/entry/encounter");
        DTMNodeList vmrElementList = getElement(vmrInputDocument, "vmrInput/patient/clinicalStatements/encounterEvents/encounterEvent");

        for (int i=0; i<eicrElementList.getLength(); i++) {
            Node eicrActNode = eicrElementList.item(i);

            Node vmrMatchingElement = findElementWithMatchingId(eicrActNode, vmrElementList);

            assertTrue("Cannot find corresponding vmr:'encounter' element for eicr:entry/encounter element", vmrMatchingElement!=null);

            for (int j = 0; j < eicrActNode.getChildNodes().getLength(); j++) {
                Node eicrActNodeElementNode = eicrActNode.getChildNodes().item(j);

                if ("code".equals(eicrActNodeElementNode.getNodeName())) {

                    DTMNodeList vmrCodeList = getElements(vmrMatchingElement, "encounterType");

                    assertTrue("code occurs >1", vmrCodeList.getLength()<=1);

                    compareElementAttributes(eicrActNodeElementNode, vmrCodeList.item(0), "code", "code");

                }

                if ("effectiveTime".equals(eicrActNodeElementNode.getNodeName())) {

                    DTMNodeList vmrEncounterEventTimeList = getElements(vmrMatchingElement, "encounterEventTime");

                    assertTrue("encounterEventTime occurs >1", vmrEncounterEventTimeList.getLength()<=1);

                    compareElementAttributes(eicrActNodeElementNode, vmrEncounterEventTimeList.item(0), "value", "low");

                }

                if ("entryRelationship".equals(eicrActNodeElementNode.getNodeName())) {
                    DTMNodeList eicrObsElements = getElements(eicrActNodeElementNode, "act/entryRelationship/observation");

                    for (int k=0; k<eicrObsElements.getLength(); k++) {
                        compareObsToProblem(eicrObsElements.item(k), getElements(vmrMatchingElement, "relatedClinicalStatement/problem"));

                    }
                }
            }
        }

    }

    @Test
    public void testProblemSection() throws Throwable, Exception {

        ClassLoader.getSystemClassLoader().loadClass("org.cdsframework.messageconverter.eicr.convert.utils.ProblemTypeCodeLookup");

        ClinicalDocument clinicalDocument = CDAUtil.load(new FileInputStream("src/test/resources/samples/good_messages/eICR_ProblemSection.xml"));
        CDSInput result = Eicr2Vmr.getCdsInputFromClinicalDocument(clinicalDocument);

        String vmrInputStr = CdsObjectAssist.cdsObjectToString(result, CDSInput.class);
        save(vmrInputStr, "src/test/resources/samples/good_messages/eICR_ProblemSection_cdsInput.xml");

        String eicrStr = new String(Files.readAllBytes(Paths.get("src/test/resources/samples/good_messages/eICR_ProblemSection.xml")));

        Document vmrInputDocument = getDocument(vmrInputStr);
        Document eicrDocument = getDocument(eicrStr);

        DTMNodeList eicrElementList = getElement(eicrDocument, "ClinicalDocument/component/structuredBody/component/section/entry/act/entryRelationship/observation[../../../../code[@code='11450-4']]");
        DTMNodeList vmrElementList = getElement(vmrInputDocument, "vmrInput/patient/clinicalStatements/problems/problem");

        compareProblemStatus(eicrDocument, vmrElementList);

        for (int i=0; i<eicrElementList.getLength(); i++) {
            Node eicrElement = eicrElementList.item(i);

            Node vmrMatchingElement = findElementWithMatchingId(eicrElement, vmrElementList);

            assertTrue("Cannot find corresponding vmr:'problem' element for eicr:entryRelationship/observation element", vmrMatchingElement!=null);

            for (int j = 0; j < eicrElement.getChildNodes().getLength(); j++) {
                Node eicrElementNode = eicrElement.getChildNodes().item(j);

                if ("value".equals(eicrElementNode.getNodeName())) {

                    DTMNodeList vmrProblemCodeList = getElements(vmrMatchingElement, "problemCode");

                    assertTrue("problemCode occurs >1", vmrProblemCodeList.getLength()<=1);

                    compareElementAttributes(eicrElementNode, vmrProblemCodeList.item(0), "code", "code");

                }

                if ("effectiveTime".equals(eicrElementNode.getNodeName())) {

                    DTMNodeList vmrProblemEffectiveTimeList = getElements(vmrMatchingElement, "problemEffectiveTime");

                    assertTrue("problemEffectiveTime occurs >1", vmrProblemEffectiveTimeList.getLength()<=1);

                    compareElementAttributes(getElements(eicrElementNode, "low").item(0), vmrProblemEffectiveTimeList.item(0), "value", "low");

                }

                if ("problemStatus".equals(eicrElementNode.getNodeName())) {
                    DTMNodeList eicrObsElements = getElements(eicrElementNode, "act/entryRelationship/observation");

                    assertTrue(getElements(vmrMatchingElement, "relatedClinicalStatement/problem")==null || getElements(vmrMatchingElement, "relatedClinicalStatement/problem").getLength()==0);

                }
            }
        }
    }

    private void compareProblemStatus(Document eicrDocument, DTMNodeList vmrElementList) throws Exception {
        DTMNodeList eicrProblemStatus = getElement(eicrDocument, "ClinicalDocument/component/structuredBody/component/section/entry/act[../../code[@code='11450-4']]");

        for (int h = 0; h < eicrProblemStatus.getLength(); h++) {
            Node eicrElementNode = eicrProblemStatus.item(h);

            if ("statusCode".equals(eicrElementNode.getNodeName())) {
                Node vmrMatchingElement = findElementWithMatchingId(eicrElementNode, vmrElementList);

                DTMNodeList vmrProblemStatusList = getElements(vmrMatchingElement, "problemStatus");

                assertTrue("problemStatus occurs >1", vmrProblemStatusList.getLength()<=1);

                compareElementAttributes(eicrElementNode, vmrProblemStatusList.item(0), "code", "code");

            }
        }
    }

    /**
     *
     *
     *
     * eicr://encounter/id/@root                vmr://encounterEvent/id@root
     * eicr://encounter/code/@code              vmr://encounterEvent/encounterType/@code
     * eicr://encounter/effectiveTime/@value    vmr://encounterEvent/encounterEventTime/@low
     * eicr://observation/id/@root              vmr://observationResult/id/@root
     * eicr://observation                       vmr://observationResult
     *
     * @throws Exception
     *
     *
     * NOTE: This test is being ignored now as it doesn't make sense.
     *
     * eicr:/cda:encounter[1]/cda:entryRelationship[1]/cda:act[1]/cda:entryRelationship[1]/cda:observation[1] maps to
     * vmr:/encounterEvents[1]/encounterEvent[1]/relatedClinicalStatement[1]/problem[1] and can either represent
     * a problem (described by the patient) or a diagnosis (diagnosed by a medical professional), discriminated by
     * the 'diagnosticEventTime' )(discovered 03/06/2017). For the time being this test will be ignored.
     *
     *
     */
    @Ignore
    @Test
    public void testEncounters2Vmr_is_not_diagnosis_29308_4() throws Exception, Throwable {
        ClinicalDocument clinicalDocument = CDAUtil.load(new FileInputStream("src/test/resources/samples/good_messages/eICR_EncountersSection_with_problem_with_wrong_code.xml"));
        CDSInput result = Eicr2Vmr.getCdsInputFromClinicalDocument(clinicalDocument);

        String vmrInputStr = CdsObjectAssist.cdsObjectToString(result, CDSInput.class);
        save(vmrInputStr, "src/test/resources/samples/eICR_EncountersSection_with_problem_with_wrong_code_cdsInput.xml");

        String eicrStr = new String(Files.readAllBytes(Paths.get("src/test/resources/samples/good_messages/eICR_EncountersSection_with_problem_with_wrong_code.xml")));

        Document vmrInputDocument = getDocument(vmrInputStr);
        Document eicrDocument = getDocument(eicrStr);

        DTMNodeList eicrElementList = getElement(eicrDocument, "ClinicalDocument/component/structuredBody/component/section/entry/encounter");
        DTMNodeList vmrElementList = getElement(vmrInputDocument, "vmrInput/patient/clinicalStatements/encounterEvents/encounterEvent");

        for (int i=0; i<eicrElementList.getLength(); i++) {
            Node eicrActNode = eicrElementList.item(i);

            Node vmrMatchingElement = findElementWithMatchingId(eicrActNode, vmrElementList);

            assertTrue("Cannot find corresponding vmr:'encounter' element for eicr:entry/encounter element", vmrMatchingElement!=null);

            for (int j = 0; j < eicrActNode.getChildNodes().getLength(); j++) {
                Node eicrActNodeElementNode = eicrActNode.getChildNodes().item(j);

                if ("code".equals(eicrActNodeElementNode.getNodeName())) {

                    DTMNodeList vmrCodeList = getElements(vmrMatchingElement, "encounterType");

                    assertTrue("code occurs >1", vmrCodeList.getLength()<=1);

                    compareElementAttributes(eicrActNodeElementNode, vmrCodeList.item(0), "code", "code");

                }

                if ("effectiveTime".equals(eicrActNodeElementNode.getNodeName())) {

                    DTMNodeList vmrEncounterEventTimeList = getElements(vmrMatchingElement, "encounterEventTime");

                    assertTrue("encounterEventTime occurs >1", vmrEncounterEventTimeList.getLength()<=1);

                    compareElementAttributes(eicrActNodeElementNode, vmrEncounterEventTimeList.item(0), "value", "low");

                }

                if ("entryRelationship".equals(eicrActNodeElementNode.getNodeName())) {
                    DTMNodeList eicrObsElements = getElements(eicrActNodeElementNode, "act/entryRelationship/observation");

                    assertTrue(getElements(vmrMatchingElement, "relatedClinicalStatement/problem")==null || getElements(vmrMatchingElement, "relatedClinicalStatement/problem").getLength()==0);

                }
            }
        }

    }

    @Ignore
    @Test
    public void testSocialHistorySection() throws Exception, Throwable {
        ClinicalDocument clinicalDocument = CDAUtil.load(new FileInputStream("src/test/resources/samples/good_messages/eICR_SocialHistorySection.xml"));
        CDSInput result = Eicr2Vmr.getCdsInputFromClinicalDocument(clinicalDocument);

        String vmrInputStr = CdsObjectAssist.cdsObjectToString(result, CDSInput.class);
        save(vmrInputStr, "src/test/resources/samples/good_messages/eICR_SocialHistorySection_cdsInput.xml");

        String eicrStr = new String(Files.readAllBytes(Paths.get("src/test/resources/samples/good_messages/eICR_SocialHistorySection.xml")));

        Document vmrInputDocument = getDocument(vmrInputStr);
        Document eicrDocument = getDocument(eicrStr);

        DTMNodeList eicrElementList = getElement(eicrDocument, "ClinicalDocument/component/structuredBody/component/section/entry/organizer/component/observation[../../../../code[@code='29762-2']]");
        DTMNodeList vmrElementList = getElement(vmrInputDocument, "clinicalStatements/observationResults/observationResult");
    }

    @Test
    public void testMedicationsSection() throws Exception, Throwable {
        ClinicalDocument clinicalDocument = CDAUtil.load(new FileInputStream("src/test/resources/samples/good_messages/eICR_MedicationsSection.xml"));
        CDSInput result = Eicr2Vmr.getCdsInputFromClinicalDocument(clinicalDocument);

        String vmrInputStr = CdsObjectAssist.cdsObjectToString(result, CDSInput.class);
        save(vmrInputStr, "src/test/resources/samples/good_messages/eICR_MedicationsSection_cdsInput.xml");

        String eicrStr = new String(Files.readAllBytes(Paths.get("src/test/resources/samples/good_messages/eICR_MedicationsSection.xml")));

        Document vmrInputDocument = getDocument(vmrInputStr);
        Document eicrDocument = getDocument(eicrStr);

        DTMNodeList eicrElementList = getElement(eicrDocument, "ClinicalDocument/component/structuredBody/component/section/entry/substanceAdministration[../../code[@code='29549-3']]");
        DTMNodeList vmrElementList = getElement(vmrInputDocument, "vmrInput/patient/clinicalStatements/substanceAdministrationEvents/substanceAdministrationEvent");

        for (int i=0; i<eicrElementList.getLength(); i++) {
            Node eicrActNode = eicrElementList.item(i);

            Node vmrMatchingElement = findElementWithMatchingId(eicrActNode, vmrElementList);

            assertTrue("Cannot find corresponding vmr:'substanceAdministrationEvent' element for eicr:substanceAdministration element", vmrMatchingElement != null);

            for (int j = 0; j < eicrActNode.getChildNodes().getLength(); j++) {
                Node eicrActNodeElementNode = eicrActNode.getChildNodes().item(j);

                if ("statusCode".equals(eicrActNodeElementNode.getNodeName())) {

                    DTMNodeList vmrSubstanceAdministrationGeneralPurposeList = getElements(vmrMatchingElement, "substanceAdministrationGeneralPurpose");

                    assertTrue("substanceAdministrationGeneralPurpose occurs >1", vmrSubstanceAdministrationGeneralPurposeList.getLength() <= 1);

                    compareElementAttributes(eicrActNodeElementNode, vmrSubstanceAdministrationGeneralPurposeList.item(0), "code", "code");

                } else if ("routeCode".equals(eicrActNodeElementNode.getNodeName())) {

                    DTMNodeList vmrDeliveryRouteList = getElements(vmrMatchingElement, "deliveryRoute");

                    assertTrue("deliveryRoute occurs >1", vmrDeliveryRouteList.getLength() <= 1);

                    compareElementAttributes(eicrActNodeElementNode, vmrDeliveryRouteList.item(0), "code", "code");

                } else if ("doseQuantity".equals(eicrActNodeElementNode.getNodeName())) {

                    DTMNodeList vmrDoseQuantityList = getElements(vmrMatchingElement, "doseQuantity");

                    assertTrue("doseQuantity occurs >1", vmrDoseQuantityList.getLength() <= 1);

                    compareElementAttributes(eicrActNodeElementNode, vmrDoseQuantityList.item(0), "value", "lowValue");
                    compareElementAttributes(eicrActNodeElementNode, vmrDoseQuantityList.item(0), "value", "highValue");
                    compareElementAttributes(eicrActNodeElementNode, vmrDoseQuantityList.item(0), "unit", "lowUnit");
                    compareElementAttributes(eicrActNodeElementNode, vmrDoseQuantityList.item(0), "unit", "highUnit");

                } else if("consumable".equals(eicrActNodeElementNode.getNodeName())) {


                }
            }
        }
    }

    /** 
     * eicr://encounter/id/@root                vmr://encounterEvent/id@root 
     * eicr://encounter/code/@code              vmr://encounterEvent/encounterType/@code 
     * eicr://encounter/effectiveTime/@value    vmr://encounterEvent/encounterEventTime/@low 
     * eicr://observation/id/@root              vmr://observationResult/id/@root 
     * eicr://observation                       vmr://observationResult 
     * @throws Exception 
     */ 
    @Test 
    public void zika_message() throws Exception, Throwable { 
        ClinicalDocument clinicalDocument = CDAUtil.load(new FileInputStream("src/test/resources/samples/good_messages/eICR-1.1-Zika-Lab-Rule-3-Detection-Test-Zika-Nucleic-Acid-Results-Positive.xml")); 
        CDSInput result = Eicr2Vmr.getCdsInputFromClinicalDocument(clinicalDocument); 
 
        String vmrInputStr = CdsObjectAssist.cdsObjectToString(result, CDSInput.class); 
        save(vmrInputStr, "src/test/resources/samples/eICR-1.1-Zika-Lab-Rule-3-Detection-Test-Zika-Nucleic-Acid-Results-Positive_cdsInput.xml"); 
 
        String eicrStr = new String(Files.readAllBytes(Paths.get("src/test/resources/samples/good_messages/eICR-1.1-Zika-Lab-Rule-3-Detection-Test-Zika-Nucleic-Acid-Results-Positive.xml"))); 
 
        Document vmrInputDocument = getDocument(vmrInputStr); 
        Document eicrDocument = getDocument(eicrStr); 
 
        DTMNodeList eicrElementList = getElement(eicrDocument, "ClinicalDocument/component/structuredBody/component/section/entry/encounter"); 
        DTMNodeList vmrElementList = getElement(vmrInputDocument, "vmrInput/patient/clinicalStatements/encounterEvents/encounterEvent"); 
 
//        for (int i=0; i<eicrElementList.getLength(); i++) { 
//            Node eicrActNode = eicrElementList.item(i); 
// 
//            Node vmrMatchingElement = findElementWithMatchingId(eicrActNode, vmrElementList); 
// 
//            assertTrue("Cannot find corresponding vmr:'encounter' element for eicr:entry/encounter element", vmrMatchingElement!=null); 
// 
//            for (int j = 0; j < eicrActNode.getChildNodes().getLength(); j++) { 
//                Node eicrActNodeElementNode = eicrActNode.getChildNodes().item(j); 
// 
//                if ("code".equals(eicrActNodeElementNode.getNodeName())) { 
// 
//                    DTMNodeList vmrCodeList = getElements(vmrMatchingElement, "encounterType"); 
// 
//                    assertTrue("code occurs >1", vmrCodeList.getLength()<=1); 
// 
//                    compareElementAttributes(eicrActNodeElementNode, vmrCodeList.item(0), "code", "code"); 
// 
//                } 
// 
//                if ("effectiveTime".equals(eicrActNodeElementNode.getNodeName())) { 
// 
//                    DTMNodeList vmrEncounterEventTimeList = getElements(vmrMatchingElement, "encounterEventTime"); 
// 
//                    assertTrue("encounterEventTime occurs >1", vmrEncounterEventTimeList.getLength()<=1); 
// 
//                    compareElementAttributes(eicrActNodeElementNode, vmrEncounterEventTimeList.item(0), "value", "low"); 
// 
//                } 
// 
//                if ("entryRelationship".equals(eicrActNodeElementNode.getNodeName())) { 
//                    DTMNodeList eicrObsElements = getElements(eicrActNodeElementNode, "act/entryRelationship/observation"); 
// 
//                    assertTrue(getElements(vmrMatchingElement, "relatedClinicalStatement/problem")==null || getElements(vmrMatchingElement, "relatedClinicalStatement/problem").getLength()==0); 
// 
//                } 
//            } 
//        } 
 
    } 
 
    @Test
    public void testResults2Vmr() throws Exception, Throwable {
        ClinicalDocument clinicalDocument = CDAUtil.load(new FileInputStream("src/test/resources/samples/good_messages/eICR_ResultsSection.xml"));
        CDSInput result = Eicr2Vmr.getCdsInputFromClinicalDocument(clinicalDocument);

        String vmrInputStr = CdsObjectAssist.cdsObjectToString(result, CDSInput.class);
        save(vmrInputStr, "src/test/resources/samples/good_messages/eICR_ResultsSection_cdsInput.xml");

        String eicrStr = new String(Files.readAllBytes(Paths.get("src/test/resources/samples/good_messages/eICR_ResultsSection.xml")));

        Document vmrInputDocument = getDocument(vmrInputStr);
        Document eicrDocument = getDocument(eicrStr);

        DTMNodeList eicrOrganizerList = getElement(eicrDocument, "ClinicalDocument/component/structuredBody/component/section/entry/organizer[../../code[@code='30954-2']]");
        DTMNodeList vmrObsOrdersList = getElement(vmrInputDocument, "clinicalStatements/observationOrders/observationOrder");

        for (int i=0; i<eicrOrganizerList.getLength(); i++) {
            Node eicrOrganizerNode = eicrOrganizerList.item(i);

            Node vmrMatchingElement = findElementWithMatchingId(eicrOrganizerNode, vmrObsOrdersList);

            assertTrue("Cannot find corresponding vmr:'observationOrder' element for eicr:organizer element", vmrMatchingElement!=null);

            for (int j = 0; j < eicrOrganizerNode.getChildNodes().getLength(); j++) {
                Node eicrOrganizerChildNode = eicrOrganizerNode.getChildNodes().item(j);

                if ("code".equals(eicrOrganizerChildNode.getNodeName())) {

                    DTMNodeList vmrObservationFocusList = getElements(vmrMatchingElement, "observationFocus");

                    assertTrue("observationFocus occurs >1", vmrObservationFocusList.getLength()<=1);

                    compareElementAttributes(eicrOrganizerChildNode, vmrObservationFocusList.item(0), "code", "code", "displayName", "displayName");

                }

                if ("component".equals(eicrOrganizerChildNode.getNodeName())) {

                    DTMNodeList eicrObservationList = getElements(eicrOrganizerChildNode, "observation");
                    assertTrue("eicr:component size > 1", eicrObservationList.getLength()==1);

                    Node eicrObservationNode = eicrObservationList.item(0);
                    for (int k = 0; k < eicrObservationNode.getChildNodes().getLength(); k++) {

                        DTMNodeList vmrObsResList = getElement(vmrInputDocument, "clinicalStatements/observationOrders/observationOrder/relatedClinicalStatement/observationResult");

                        Node vmrMatchingObsRes = findElementWithMatchingId(eicrObservationNode, vmrObsResList);

                        assertTrue("Cannot find corresponding vmr:'observationResult' element for eicr:observation element", vmrMatchingObsRes!=null);

                        if ("code".equals(eicrObservationNode.getChildNodes().item(k).getNodeName())) {

                            DTMNodeList vmrObservationFocusList = getElements(vmrMatchingObsRes, "observationFocus");

                            assertTrue("observationFocus occurs >1", vmrObservationFocusList.getLength()<=1);

                            compareEffectiveTimeElement(eicrObservationNode.getChildNodes().item(k), vmrObservationFocusList.item(0));


                        }

                        if ("effectiveTime".equals(eicrObservationNode.getChildNodes().item(k).getNodeName())) {

                            DTMNodeList vmrProblemEffectiveTimeList = getElements(vmrMatchingObsRes, "problemEffectiveTime");

                            assertTrue("problemEffectiveTime occurs >1", vmrProblemEffectiveTimeList.getLength()<=1);

                            compareEffectiveTimeElement(eicrObservationNode.getChildNodes().item(k), vmrProblemEffectiveTimeList.item(0));

                        }

                        if ("interpretationCode".equals(eicrObservationNode.getChildNodes().item(k).getNodeName())) {

                            DTMNodeList vmrInterpretationList = getElements(vmrMatchingObsRes, "interpretation");

                            assertTrue("interpretation occurs >1", vmrInterpretationList.getLength()<=1);

                            compareElementAttributes(eicrObservationNode.getChildNodes().item(k), vmrInterpretationList.item(0), "code", "code");

                        }

                        if ("value".equals(eicrObservationNode.getChildNodes().item(k).getNodeName())) {

                            String type = getNodeAttributeValue(eicrObservationNode.getChildNodes().item(k), "xsi:type");

                            if ("PQ".equals(type)) {
                                DTMNodeList vmrPhysicalQuantityList = getElements(vmrMatchingObsRes, "observationValue/physicalQuantity");

                                assertTrue(">1 physicalQuantity found", vmrPhysicalQuantityList.getLength()==1);

                                compareElementAttributes(eicrObservationNode.getChildNodes().item(k), vmrPhysicalQuantityList.item(0), "value", "value");
                                compareElementAttributes(eicrObservationNode.getChildNodes().item(k), vmrPhysicalQuantityList.item(0), "unit", "unit");

                            } else if ("CO".equals(type)) {
                                DTMNodeList vmrConceptList = getElements(vmrMatchingObsRes, "observationValue/concept");

                                assertTrue(">1 concept found", vmrConceptList.getLength()==1);

                                compareElementAttributes(eicrObservationNode.getChildNodes().item(k), vmrConceptList.item(0), "code", "code");
                                compareElementAttributes(eicrObservationNode.getChildNodes().item(k), vmrConceptList.item(0), "codeSystem", "codeSystem");

                            } else if ("IVL_PQ".equals(type)) {
                                DTMNodeList eictLowElementList = getElements(eicrObservationNode.getChildNodes().item(k), "low");
                                DTMNodeList eictHighElementList = getElements(eicrObservationNode.getChildNodes().item(k), "high");

                                assertTrue(">1 low element found", eictLowElementList.getLength()==1);
                                assertTrue(">1 high element found", eictHighElementList.getLength()==1);

                                DTMNodeList vmrPhysicalQuantityRangeList = getElements(vmrMatchingObsRes, "observationValue/physicalQuantityRange");

                                assertTrue(">1 physicalQuantityRange element found", vmrPhysicalQuantityRangeList.getLength()==1);

                                compareElementAttributes(eictLowElementList.item(0), vmrPhysicalQuantityRangeList.item(0), "value", "lowValue");
                                compareElementAttributes(eictLowElementList.item(0), vmrPhysicalQuantityRangeList.item(0), "unit", "lowUnit");
                                compareElementAttributes(eictHighElementList.item(0), vmrPhysicalQuantityRangeList.item(0), "value", "highValue");
                                compareElementAttributes(eictHighElementList.item(0), vmrPhysicalQuantityRangeList.item(0), "unit", "highUnit");

                            }
                        }
                    }
                }
//
//                if ("effectiveTime".equals(eicrActNodeElementNode.getNodeName())) {
//
//                    DTMNodeList vmrProblemEffectiveTimeList = getElements(vmrMatchingElement, "problemEffectiveTime");
//
//                    assertTrue("problemEffectiveTime occurs >1", vmrProblemEffectiveTimeList.getLength()<=1);
//
//                    compareEffectiveTimeElement(eicrActNodeElementNode, vmrProblemEffectiveTimeList.item(0));
//
//                }
//
//                if ("interpretationCode".equals(eicrActNodeElementNode.getNodeName())) {
//
//                    DTMNodeList vmrInterpretationList = getElements(vmrMatchingElement, "interpretation");
//
//                    assertTrue("interpretation occurs >1", vmrInterpretationList.getLength()<=1);
//
//                    compareElementAttributes(eicrActNodeElementNode, vmrInterpretationList.item(0), "code", "code");
//
//                }
//
//                if ("value".equals(eicrActNodeElementNode.getNodeName())) {
//
//                    String type = getNodeAttributeValue(eicrActNodeElementNode, "xsi:type");
//
//                    if ("PQ".equals(type)) {
//                        DTMNodeList vmrPhysicalQuantityList = getElements(vmrMatchingElement, "observationValue/physicalQuantity");
//
//                        assertTrue(">1 physicalQuantity found", vmrPhysicalQuantityList.getLength()==1);
//
//                        compareElementAttributes(eicrActNodeElementNode, vmrPhysicalQuantityList.item(0), "value", "value");
//                        compareElementAttributes(eicrActNodeElementNode, vmrPhysicalQuantityList.item(0), "unit", "unit");
//
//                    } else if ("CO".equals(type)) {
//                        DTMNodeList vmrConceptList = getElements(vmrMatchingElement, "observationValue/concept");
//
//                        assertTrue(">1 concept found", vmrConceptList.getLength()==1);
//
//                        compareElementAttributes(eicrActNodeElementNode, vmrConceptList.item(0), "code", "code");
//                        compareElementAttributes(eicrActNodeElementNode, vmrConceptList.item(0), "codeSystem", "codeSystem");
//
//                    } else if ("IVL_PQ".equals(type)) {
//                        DTMNodeList eictLowElementList = getElements(eicrActNodeElementNode, "low");
//                        DTMNodeList eictHighElementList = getElements(eicrActNodeElementNode, "high");
//
//                        assertTrue(">1 low element found", eictLowElementList.getLength()==1);
//                        assertTrue(">1 high element found", eictHighElementList.getLength()==1);
//
//                        DTMNodeList vmrPhysicalQuantityRangeList = getElements(vmrMatchingElement, "observationValue/physicalQuantityRange");
//
//                        assertTrue(">1 physicalQuantityRange element found", vmrPhysicalQuantityRangeList.getLength()==1);
//
//                        compareElementAttributes(eictLowElementList.item(0), vmrPhysicalQuantityRangeList.item(0), "value", "lowValue");
//                        compareElementAttributes(eictLowElementList.item(0), vmrPhysicalQuantityRangeList.item(0), "unit", "lowUnit");
//                        compareElementAttributes(eictHighElementList.item(0), vmrPhysicalQuantityRangeList.item(0), "value", "highValue");
//                        compareElementAttributes(eictHighElementList.item(0), vmrPhysicalQuantityRangeList.item(0), "unit", "highUnit");
//
//                    }
//                }
//
            }
        }

    }

    @Test
    public void testUniquenessOfCdsInputIds() throws Exception {

        Map<String, Set<String>> idMap = new HashMap<>();

        File dir = new File("src/test/resources/samples/good_messages");
        File[] filesList = dir.listFiles();
        for (File file : filesList) {
            if (file.isFile() && file.getName().endsWith("_cdsInput.xml")) {
                logger.debug("*********************\n" + "Starting " + file.getName() + "\n*********************\n");

                InputStream inputStream = new FileInputStream(file);
                CDSInput cdsInput = CdsObjectAssist.cdsObjectFromStream(inputStream, CDSInput.class);
                String cdsInputStr = CdsObjectAssist.cdsObjectToString(cdsInput, CDSInput.class);

                Document vmrInputDocument = getDocument(cdsInputStr);

                DTMNodeList idList = getElement(vmrInputDocument, "id");

                for (int i = 0; i < idList.getLength(); i++) {
                    Node idNode = idList.item(i);

                    String root = getNodeAttributeValue(idNode, "root");
                    String ext = getNodeAttributeValue(idNode, "extension");

                    Set<String> extensionSet = idMap.get(root);

                    if (extensionSet == null) {
                        extensionSet = new HashSet<>();

                        logger.debug("adding root=" + root);

                        idMap.put(root, extensionSet);

                    }

                    logger.debug("adding ext=" + ext + " to root=" + root);

                    if (!extensionSet.add(ext)) {

                        logger.debug("Duplicate found: root=" + root + ", ext=" + ext);

                        assert false;

                    }
                }
            }

            logger.debug("\n*********************\n" + file.getName() + " - found no duplicate ids\n*********************\n");

            idMap = new HashMap<>();

        }
    }

    @Test
    public void testDuplicateIdsFromEicr() throws Throwable {

        Map<String, Set<String>> idMap = new HashMap<>();

        ClinicalDocument clinicalDocument = CDAUtil.load(new FileInputStream("src/test/resources/samples/good_messages/eICR_duplicate_ids.xml"));
        CDSInput result = Eicr2Vmr.getCdsInputFromClinicalDocument(clinicalDocument);

        String vmrInputStr = CdsObjectAssist.cdsObjectToString(result, CDSInput.class);
        save(vmrInputStr, "src/test/resources/samples/good_messages/eICR_duplicate_ids_cdsInput.xml");

        Document vmrInputDocument = getDocument(vmrInputStr);

        DTMNodeList idList = getElement(vmrInputDocument, "id");

        for (int i = 0; i < idList.getLength(); i++) {
            Node idNode = idList.item(i);

            String root = getNodeAttributeValue(idNode, "root");
            String ext = getNodeAttributeValue(idNode, "extension");

            Set<String> extensionSet = idMap.get(root);

            if (extensionSet == null) {
                extensionSet = new HashSet<>();

                logger.debug("adding root=" + root);

                idMap.put(root, extensionSet);

            }

            logger.debug("adding ext=" + ext + " to root=" + root);

            if (!extensionSet.add(ext)) {

                logger.debug("Duplicate found: root=" + root + ", ext=" + ext);

                assert false;

            }
        }
    }

    private void compareObservation(Node eicrElement, DTMNodeList vmrElementList) throws Exception {

        Node vmrNode = findElementWithMatchingId(eicrElement, vmrElementList);

        for (int j=0; j<eicrElement.getChildNodes().getLength(); j++) {
            Node eicrChildNode = eicrElement.getChildNodes().item(j);

            if ("code".equals(eicrChildNode.getNodeName())) {
                DTMNodeList obsFocusList = getElements(vmrNode, "observationFocus");

                assertTrue("ObservationFocus occurs >1", obsFocusList.getLength()<=1);

                compareElementAttributes(eicrChildNode, obsFocusList.item(0), "code", "code");

            }

            if ("statusCode".equals(eicrChildNode.getNodeName())) {
                DTMNodeList obsFocusList = getElements(vmrNode, "interpretation");

                assertTrue("Interpretation occurs >1", obsFocusList.getLength()<=1);

                compareElementAttributes(eicrChildNode, obsFocusList.item(0), "code", "code");

            }

            if ("effectiveTime".equals(eicrChildNode.getNodeName())) {
                DTMNodeList eicrLowElements = getElements(eicrChildNode, "low");
                DTMNodeList vmrObservationEventTimeList = getElements(vmrNode, "observationEventTime");

                assertTrue("observationEventTime occurs >1", vmrObservationEventTimeList.getLength()<=1);
                assertTrue("effectiveTime/low occurs >1", eicrLowElements.getLength()<=1);

                compareElementAttributes(eicrLowElements.item(0), vmrObservationEventTimeList.item(0), "value", "low");

                DTMNodeList eicrHighElements = getElements(eicrChildNode, "high");

                assertTrue("effectiveTime/high occurs >1", eicrHighElements.getLength()<=1);

                if (eicrHighElements.getLength()>0) {
                    compareElementAttributes(eicrHighElements.item(0), vmrObservationEventTimeList.item(0), "value", "high");

                } else {
                    logger.info("eicr:entry/act/effectiveDate[high] does not exist");

                }

            }

            if ("value".equals(eicrChildNode.getNodeName())) {
                DTMNodeList vmrObservationValueList = getElements(vmrNode, "observationValue");

                assertTrue("observationValue occurs >1", vmrObservationValueList.getLength()<=1);

                DTMNodeList vmrConceptList = getElements(vmrObservationValueList.item(0), "concept");

                assertTrue("observationValue/concept occurs >1", vmrConceptList.getLength()<=1);

                if (vmrConceptList.getLength()>0) {
                    compareElementAttributes(eicrChildNode, vmrConceptList.item(0), "code", "code");

                } else {
                    logger.info("vmr:problem/relatedClinicalStatement/observationResult/observationValue/concept does not exist");


                }
            }

        }
    }

    private void compareObsToProblem(Node eicrElement, DTMNodeList vmrElementList) throws Exception {

        Node vmrNode = findElementWithMatchingId(eicrElement, vmrElementList);

        for (int j=0; j<eicrElement.getChildNodes().getLength(); j++) {
            Node eicrChildNode = eicrElement.getChildNodes().item(j);

            if ("statusCode".equals(eicrChildNode.getNodeName())) {
                DTMNodeList obsFocusList = getElements(vmrNode, "problemStatus");

                assertTrue("problemStatus occurs >1", obsFocusList.getLength()<=1);

                compareElementAttributes(eicrChildNode, obsFocusList.item(0), "code", "code");

            }

            if ("effectiveTime".equals(eicrChildNode.getNodeName())) {
                DTMNodeList eicrLowElements = getElements(eicrChildNode, "low");
                DTMNodeList vmrProblemEffectiveTimeList = getElements(vmrNode, "problemEffectiveTime");

                assertTrue("problemEffectiveTime occurs >1", vmrProblemEffectiveTimeList.getLength()<=1);
                assertTrue("effectiveTime/low occurs >1", eicrLowElements.getLength()<=1);

                compareElementAttributes(eicrLowElements.item(0), vmrProblemEffectiveTimeList.item(0), "value", "low");

                DTMNodeList eicrHighElements = getElements(eicrChildNode, "high");

                assertTrue("problemEffectiveTime/high occurs >1", eicrHighElements.getLength()<=1);

                if (eicrHighElements.getLength()>0) {
                    compareElementAttributes(eicrHighElements.item(0), vmrProblemEffectiveTimeList.item(0), "value", "high");

                } else {
                    logger.info("eicr:entry/act/effectiveDate[high] does not exist");

                }

            }

            if ("value".equals(eicrChildNode.getNodeName())) {
                DTMNodeList vmrProblemCodeList = getElements(vmrNode, "problemCode");

                assertTrue("problemCode occurs >1", vmrProblemCodeList.getLength()<=1);

                if (vmrProblemCodeList.getLength()>0) {
                    compareElementAttributes(eicrChildNode, vmrProblemCodeList.item(0), "code", "code");
                    compareElementAttributes(eicrChildNode, vmrProblemCodeList.item(0), "codeSystem", "codeSystem");
                    compareElementAttributes(eicrChildNode, vmrProblemCodeList.item(0), "displayName", "displayName");

                } else {
                    logger.info("vmr:problem/relatedClinicalStatement/observationResult/observationValue/concept does not exist");


                }
            }

        }
    }

    private void compareElementAttributes(Node eicrCodeElement, Node vmrProblemCodeElement, String... attribNames) {

        for (int i=0; i<attribNames.length; i=i+2) {

            String eicrElementValue = getNodeAttributeValue(eicrCodeElement, attribNames[i]);
            String vmrElementValue = getNodeAttributeValue(vmrProblemCodeElement, attribNames[i+1]);
            
            if (NumberUtils.isNumber(eicrElementValue) && NumberUtils.isNumber(vmrElementValue)) {
                assertEquals("Values for 'eicr:" + eicrCodeElement.getNodeName() + "[" + attribNames[i] + "]' and 'vmr:" + vmrProblemCodeElement.getNodeName() + "[" + attribNames[i + 1] + "]' are not equal",
                        NumberUtils.createFloat(eicrElementValue).toString(), NumberUtils.createFloat(vmrElementValue).toString());

            } else {
                assertEquals("Values for 'eicr:" + eicrCodeElement.getNodeName() + "[" + attribNames[i] + "]' and 'vmr:" + vmrProblemCodeElement.getNodeName() + "[" + attribNames[i + 1] + "]' are not equal",
                        eicrElementValue, vmrElementValue);

            }
        }

    }

    private void compareEffectiveTimeElement(Node eicrCodeElement, Node vmrProblemCodeElement) {

        for (int i=0; i<eicrCodeElement.getChildNodes().getLength(); i++) {

            Node eicrEffectiveTimeChildElement = eicrCodeElement.getChildNodes().item(i);

            if ("low".equals(eicrEffectiveTimeChildElement.getNodeName())) {
                assertEquals("eicr:act/effectiveDate/low[value] does not match vmr:problem/problemEffectiveDate[low]", getNodeAttributeValue(eicrEffectiveTimeChildElement, "value"), getNodeAttributeValue(vmrProblemCodeElement, "low"));

            }

            if ("high".equals(eicrEffectiveTimeChildElement.getNodeName())) {
                assertEquals("eicr:act/effectiveDate/high[value] does not match vmr:problem/problemEffectiveDate[high]", getNodeAttributeValue(eicrEffectiveTimeChildElement, "value"), getNodeAttributeValue(vmrProblemCodeElement, "high"));

            }
        }
    }

    private Node findElementWithMatchingId(Node eicrActNodeElementNode, DTMNodeList vmrElementList) throws Exception{

        DTMNodeList eicrActIdNodeElementNode = getElements(eicrActNodeElementNode, "id");

        assertTrue("Found >1 id element in eICR message", eicrActIdNodeElementNode.getLength()==1);

        Node idNode = eicrActIdNodeElementNode.item(0);

        List<Node> foundList = getElements(vmrElementList, "id", getNodeAttributeValue(idNode, "root"));

        assertTrue("Found "+foundList.size()+" id element in vMR message where root=" + getNodeAttributeValue(idNode, "root"), foundList.size()==1);

        return foundList.get(0);

    }

    private String getNodeAttributeValue(Node node, String nodeName) {
        for (int i=0; i<node.getAttributes().getLength(); i++) {
            if (nodeName.equals(node.getAttributes().item(i).getNodeName())) {
                return node.getAttributes().item(i).getNodeValue();
            }
        }

        return "";

    }

    private void compareAddresses(Node vmrInputParentNode, DTMNodeList eicrAddressParent) {

        logger.debug("starting compareAddresses()...");

        int vmrAddressPartCount = 0;
        int eicrAddressPartCount = 0;

        for(int h=0; h<eicrAddressParent.getLength(); h++) {
            Node eicrAddress = eicrAddressParent.item(h);

            if (!isAddressTypeTheSame(vmrInputParentNode, eicrAddress)) {
                continue;
            }

            for (int i=0; i<vmrInputParentNode.getChildNodes().getLength(); i++) {

                if ("part".equals(vmrInputParentNode.getChildNodes().item(i).getNodeName())) {

                    vmrAddressPartCount++;

                    Node vmrInputAddressPart = vmrInputParentNode.getChildNodes().item(i);

                    String vmrAddrPartType = vmrInputAddressPart.getAttributes().getNamedItem("type").getNodeValue();
                    String vmrAddrPartValue = vmrInputAddressPart.getAttributes().getNamedItem("value").getNodeValue();

                    String eicrAddressPartType = addressParts.get(vmrInputAddressPart.getAttributes().getNamedItem("type").getNodeValue());

                    for (int j=0; j<eicrAddress.getChildNodes().getLength(); j++) {
                        if (eicrAddressPartType.equals(eicrAddress.getChildNodes().item(j).getNodeName())) {
                            eicrAddressPartCount++;
                            assertEquals(vmrAddrPartValue, eicrAddress.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());

                        }
                    }
                }
            }

            assertEquals(eicrAddressPartCount, vmrAddressPartCount);

        }

    }

    private boolean isAddressTypeTheSame(Node vmrInputParentNode, Node eicrAddress) {

        if (vmrInputParentNode.getAttributes()!=null && vmrInputParentNode.getAttributes().getNamedItem("use")!=null
                && eicrAddress.getAttributes()!=null && eicrAddress.getAttributes().getNamedItem("use")!=null) {

            logger.info("Comparing vmr:address of type " + vmrInputParentNode.getAttributes().getNamedItem("use").getNodeValue()
                    + " with eicr:addr of type " + eicrAddress.getAttributes().getNamedItem("use").getNodeValue());

            return (StringUtils.equals(eicrAddress.getAttributes().getNamedItem("use").getNodeValue(),
                    vmrInputParentNode.getAttributes().getNamedItem("use").getNodeValue()));

        } else if (vmrInputParentNode.getAttributes()!=null && vmrInputParentNode.getAttributes().getNamedItem("use")==null
                && eicrAddress.getAttributes()!=null && eicrAddress.getAttributes().getNamedItem("use")==null) {

            logger.info("vmr:address and eicr:addr have no 'use'");

            return true;

        } else {
            logger.info("vmr:address and eicr:addr are of diff use types");

        }

        return false;

    }

    private DTMNodeList getElement(Document document, String elementName) throws Exception {
        XPathExpression expr = xpathFactory.newXPath().compile("//" + elementName);
        return (DTMNodeList)expr.evaluate(document, XPathConstants.NODESET);

    }

    private DTMNodeList getElements(Node node, String nodeName) throws Exception {

        if (node==null) {
            fail("node is null");

        }

        XPathExpression expr = xpathFactory.newXPath().compile(nodeName);

        DTMNodeList result = null;
        try {
             result = (DTMNodeList) expr.evaluate(node, XPathConstants.NODESET);

        } catch (XPathExpressionException e) {
            fail(nodeName + " cannot be found on node: " + node.getNodeName());

        }

        return result;

//        for (int i=0; i<node.getChildNodes().getLength(); i++) {
//            if (nodeName.equals(node.getChildNodes().item(i).getNodeName())) {
//                return node.getChildNodes().item(i);
//            }
//        }

    }

    private List<Node> getElements(DTMNodeList nodeList, String attribName, String attribValue) throws Exception {

        if (nodeList==null) {
            fail("nodeList is null");

        }

        List dtmNodeList = new ArrayList();

        for (int i=0; i<nodeList.getLength(); i++) {
            Node element = nodeList.item(i);

            for (int j=0; j<element.getChildNodes().getLength(); j++) {
                Node childNode = element.getChildNodes().item(j);

                if (attribName.equals(childNode.getNodeName())) {

                    if (getNodeAttributeValue(childNode, "root").equals(attribValue)) {
                        // we have found the vmr node, now compare
                        dtmNodeList.add(element);

                    }

                } else {
                    continue;

                }
            }
        }

        return dtmNodeList;

    }

    private String getElementAttribute(Document eicrDocument, String element, String attribute) throws Exception {

        XPathExpression expr = xpathFactory.newXPath().compile("//" + element);
        Object eicrResult = expr.evaluate(eicrDocument, XPathConstants.NODESET);

        if (((DTMNodeList) eicrResult).getLength()>0
                && ((DTMNodeList) eicrResult).item(0).getAttributes()!=null
                && ((DTMNodeList) eicrResult).item(0).getAttributes().getNamedItem(attribute)!=null) {

            return ((DTMNodeList) eicrResult).item(0).getAttributes().getNamedItem(attribute).getNodeValue();

        } else {
            return "";

        }
    }

    private String getDateOfBirth(Document eicrDocument) throws Exception {

        XPathExpression expr = xpathFactory.newXPath().compile("//birthTime");
        Object eicrResult = expr.evaluate(eicrDocument, XPathConstants.NODESET);

        if (((DTMNodeList) eicrResult).getLength()>0
                && ((DTMNodeList) eicrResult).item(0).getAttributes()!=null
                && ((DTMNodeList) eicrResult).item(0).getAttributes().getNamedItem("value")!=null) {

            return ((DTMNodeList) eicrResult).item(0).getAttributes().getNamedItem("value").getNodeValue();

        } else {
            return "";

        }
    }

    private String getDateOfBirth(NodeList childNodeList, int j) throws ParseException {
        NamedNodeMap attribs = childNodeList.item(j).getAttributes();

        for (int l = 0; l < attribs.getLength(); l++) {
            Node attrib = attribs.item(l);

            if ("value".equals(attrib.getNodeName())) {
                return attrib.getNodeValue();

            }
        }

        return "";

    }

    public String getGender(NodeList childNodeList, int j) {
        NamedNodeMap attribs = childNodeList.item(j).getAttributes();

        for (int l = 0; l < attribs.getLength(); l++) {
            Node attrib = attribs.item(l);

            if ("code".equals(attrib.getNodeName())) {
                return attrib.getNodeValue();

            }
        }

        return "";

    }

    private Document getDocument(String docAsString) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);

        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.parse(new ByteArrayInputStream(docAsString.getBytes()));

    }

    @Ignore
    @Test
    public void idRandomizer() throws Exception {

        String eicrStr = new String(Files.readAllBytes(Paths.get("src/test/resources/samples/eICR_ProblemSection.xml")));

        Document eicrDocument = getDocument(eicrStr);

        DTMNodeList eicrElementList = getElement(eicrDocument, "id");

        for (int i=0; i<eicrElementList.getLength(); i++) {
            Node idNode = eicrElementList.item(i);

            boolean foundExtension=false;

            for (int j=0; j<idNode.getAttributes().getLength(); j++) {
                if ("root".equals(idNode.getAttributes().item(j).getNodeName())) {
                    idNode.getAttributes().item(j).setNodeValue(randomizeId());

                }

                if ("extension".equals(idNode.getAttributes().item(j).getNodeName())) {
                    idNode.getAttributes().item(j).setNodeValue("EICR-to-VMR TESTING");
                    foundExtension=true;

                }
            }

            if (!foundExtension) {
                ((Element)idNode).setAttribute("extension", "EICR-to-VMR TESTING");

            }

        }

        save(eicrDocument, "src/test/resources/samples/eICR_ProblemSection_output.xml");

    }

    private void save(Document eicrDocument, String pathAndFilename) throws Exception {
        File output = new File(pathAndFilename);
        FileOutputStream fileOutputStream = new FileOutputStream(output);
        fileOutputStream.write(xmlToString(eicrDocument).getBytes());

    }

    private void save(String docAsString, String pathAndFilename) throws Exception {
        File output = new File(pathAndFilename);
        FileOutputStream fileOutputStream = new FileOutputStream(output);
        fileOutputStream.write(docAsString.getBytes());

    }

    private String randomizeId() {
        UUID uid = UUID.fromString("ec8a6ff8-ed4b-4f7e-82c3-e98e58b45de7");
        return uid.randomUUID().toString();

    }

    public static String xmlToString(Node node) throws TransformerException{
        Source source = new DOMSource(node);
        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult(stringWriter);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.transform(source, result);

        return stringWriter.getBuffer().toString();

    }

    @Test
    @Ignore
     public void testDateTesting() throws Exception {

        String dateStr = "20160231";

        Pattern pattern = Pattern.compile("((19|20)\\d\\d)(0?[1-9]|1[012])(0?[1-9]|[12][0-9]|3[01])");

        boolean result = pattern.matcher(dateStr).matches();

        System.out.println(result);

    }

    /**
     * Test of getCdsInputFromClinicalDocument method of class Eicr2Vmr for 'Record Target' section.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void workingTest() throws Exception, Throwable {

        ClinicalDocument clinicalDocument = CDAUtil.load(new FileInputStream("src/test/resources/samples/good_messages/eICR-1.1-Zika-Rule-1_Provider_Diagnosis-Zika-3928002_Problem-Blank.xml"));
        CDSInput result = Eicr2Vmr.getCdsInputFromClinicalDocument(clinicalDocument);

        String vmrInputStr = CdsObjectAssist.cdsObjectToString(result, CDSInput.class);
        save(vmrInputStr, "src/test/resources/samples/good_messages/eICR-1.1-Zika-Rule-1_Provider_Diagnosis-Zika-3928002_Problem-Blank_cdsInput.xml");

    }

}
