package org.cdsframework.messageconverter

import org.cdsframework.messageconverter.util.OpencdsClient
import spock.lang.Specification
import spock.lang.Unroll

public class ConvertedCdsInputTests extends Specification {

    /**
     *
     * test mechanism to fire request to opencds with converted eicr message. these
     * messages should render a cdsOutput message with an RCKMS_reporting_indicator
     *
     */
    @Unroll
    def "test cdsinput_converted_eicr_messages_that_produce_RCKMS_reporting_indicator"() {

        when:
        def cdsInputFiles = [
                                "eICR_EncountersSection_with_problem_cdsInput.xml",
                                "eICR_ResultsSection_cdsInput.xml",
                                "eICR_ProblemSection_cdsInput.xml"
                            ].each {

            println "processing $it"

            def input = this.getClass().getResource("/samples/good_messages/$it").text

            def params = [
                    kmEvaluationRequest:[scopingEntityId: 'gov.cdc.rckms.nv', businessId: 'RCKMS', version: '1.0.0'],
                    specifiedTime: '2012-01-01'
            ]
            def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)

            def cdsOutput = new XmlSlurper().parseText(responsePayload)

            assert cdsOutput instanceof groovy.util.slurpersupport.GPathResult

            def filename = "$it".toString().tokenize('.')
            def cdsOutputFile = new File("src/test/resources/samples/good_messages/" + filename[0] + "_cdsOutput.xml")
            cdsOutputFile.write(responsePayload)

            def found = false;

            cdsOutput.vmrOutput.patient.clinicalStatements.observationResults.children().each {

                if(it.observationFocus.@code == "RCKMS_Reporting_indicator") {
                    found = true;

                }
            }

            if (found) {
                println("Found observation result with RCKMS reporting indicator")
                assert true

            } else {
                assert false

            }
        }

        then:
        println('*************************')
        println('cdsInput message test complete:')
        println('*************************')

    }

    /**
     *
     * test mechanism to fire request to opencds with converted eicr message. these
     * messages should render a cdsOutput message with NO RCKMS_reporting_indicator
     *
     */
    @Unroll
    def "test cdsinput_converted_eicr_messages_that_DO_NOT_produce_RCKMS_reporting_indicator"() {

        when:
            def cdsInputFiles = ["eICR_DemographicsSection_cdsInput.xml",
                                 "eICR_HealthCareFacility_cdsInput.xml",
                                 "eICR_MedicationsSection_cdsInput.xml"
        ].each {

            println "processing $it"

            def input = this.getClass().getResource("/samples/good_messages/$it").text

            def params = [
                    kmEvaluationRequest:[scopingEntityId: 'gov.cdc.rckms.nv', businessId: 'RCKMS', version: '1.0.0'],
                    specifiedTime: '2012-01-01'
            ]
            def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)

            def cdsOutput = new XmlSlurper().parseText(responsePayload)

            assert cdsOutput instanceof groovy.util.slurpersupport.GPathResult

        }

        then:
        println('*************************')
        println('cdsInput message test complete:')
        println('*************************')

    }
}