/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import groovy.json.JsonSlurper
import org.codenarc.AnalysisContext
import org.codenarc.results.DirectoryResults
import org.codenarc.results.FileResults
import org.codenarc.rule.StubRule
import org.codenarc.rule.Violation
import org.codenarc.rule.imports.DuplicateImportRule
import org.codenarc.rule.unnecessary.UnnecessaryBooleanInstantiationRule
import org.codenarc.ruleset.ListRuleSet
import org.codenarc.test.AbstractTestCase
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

/**
 * Tests for CodeClimateReportWriter
 *
 * @author Jeremy Isikoff
 */
class CodeClimateReportWriterTest extends AbstractTestCase {

    protected static final LINE1 = 111
    protected static final LINE2 = 222
    protected static final LINE3 = 333
    protected static final SOURCE_LINE1 = 'if (count < 23 && index <= 99 && name.contains(\'\u0000\')) {'
    protected static final SOURCE_LINE3 = 'throw new Exception("cdata=<![CDATA[whatever]]>") // Some very long message 1234567890123456789012345678901234567890'
    protected static final MESSAGE2 = 'bad stuff: !@#$%^&*()_+<>'
    protected static final MESSAGE3 = 'Other info'
    private static final VIOLATION1 = new Violation(rule:new StubRule(name:'RULE1', priority:1), lineNumber:LINE1, sourceLine:SOURCE_LINE1)
    private static final VIOLATION2 = new Violation(rule:new StubRule(name:'RULE2', priority:2), lineNumber:LINE2, message:MESSAGE2)
    private static final VIOLATION3 = new Violation(rule:new StubRule(name:'RULE3', priority:3), lineNumber:LINE3, sourceLine:SOURCE_LINE3, message:MESSAGE3 )
    protected static final SRC_DIR1 = 'c:/MyProject/src/main/groovy'
    protected static final SRC_DIR2 = 'c:/MyProject/src/test/groovy'
    private static final NEW_REPORT_FILE = 'target/NewCodeClimateReport.json'

    protected reportWriter
    protected analysisContext
    protected results, srcMainDaoDirResults
    protected ruleSet
    protected stringWriter

    @SuppressWarnings('LineLength')
            private static final REPORT_JSON = """{
            "type": "issue",
            "check_name": "RULE1",
            "description": "No description provided for rule named [RULE1]",
            "content": {"body":"No description provided for rule named [RULE1]"},
            "categories": [
                "Style"
            ],
            "location": {
                "path": "src/main/MyAction.groovy",
                "lines": {
                    "begin": 111,
                    "end": 111
                }
            },
            "remediation_points": 50000,
            "severity": "critical"
        }\u0000
        {
            "type": "issue",
            "check_name": "RULE3",
            "description": "Other info",
            "content": {"body":"No description provided for rule named [RULE3]"},
            "categories": [
                "Style"
            ],
            "location": {
                "path": "src/main/MyAction.groovy",
                "lines": {
                    "begin": 333,
                    "end": 333
                }
            },
            "remediation_points": 50000,
            "severity": "minor"
        }\u0000
        {
            "type": "issue",
            "check_name": "RULE3",
            "description": "Other info",
            "content": {"body":"No description provided for rule named [RULE3]"},
            "categories": [
                "Style"
            ],
            "location": {
                "path": "src/main/MyAction.groovy",
                "lines": {
                    "begin": 333,
                    "end": 333
                }
            },
            "remediation_points": 50000,
            "severity": "minor"
        }\u0000
        {
            "type": "issue",
            "check_name": "RULE1",
            "description": "No description provided for rule named [RULE1]",
            "content": {"body":"No description provided for rule named [RULE1]"},
            "categories": [
                "Style"
            ],
            "location": {
                "path": "src/main/MyAction.groovy",
                "lines": {
                    "begin": 111,
                    "end": 111
                }
            },
            "remediation_points": 50000,
            "severity": "critical"
        }\u0000
        {
            "type": "issue",
            "check_name": "RULE2",
            "description": "bad stuff: !@#\$%^&*()_+<>",
            "content": {"body":"No description provided for rule named [RULE2]"},
            "categories": [
                "Style"
            ],
            "location": {
                "path": "src/main/MyAction.groovy",
                "lines": {
                    "begin": 222,
                    "end": 222
                }
            },
            
            "severity": "major"
        }\u0000
        {
            "type": "issue",
            "check_name": "RULE3",
            "description": "Other info",
            "content": {"body":"No description provided for rule named [RULE3]"},
            "categories": [
                "Style"
            ],
            "location": {
                "path": "src/main/dao/MyDao.groovy",
                "lines": {
                    "begin": 333,
                    "end": 333
                }
            },
            "remediation_points": 50000,
            "severity": "minor"
        }\u0000
        {
            "type": "issue",
            "check_name": "RULE2",
            "description": "bad stuff: !@#\$%^&*()_+<>",
            "content": {"body":"No description provided for rule named [RULE2]"},
            "categories": [
                "Style"
            ],
            "location": {
                "path": "src/main/dao/MyOtherDao.groovy",
                "lines": {
                    "begin": 222,
                    "end": 222
                }
            },
            "remediation_points": 50000,
            "severity": "major"
        }\u0000
    """

    @SuppressWarnings('JUnitStyleAssertions')
    protected void assertJson(String actualJson, String expectedJson) {
        log(actualJson)
        assertEquals(normalizeJson(expectedJson), normalizeJson(actualJson))
    }

    protected String normalizeJson(String json) {
        new JsonSlurper().parseText(json.replaceAll('/0', ',')).toString()
    }

    @Test
    void testWriteReport_Writer() {
        reportWriter.writeReport(stringWriter, analysisContext, results)
        def jsonAsString = stringWriter.toString()
        assertJson(jsonAsString, REPORT_JSON)
    }

    @Test
    void testWriteReport_WritesToDefaultReportFile() {
        reportWriter.writeToStandardOut = false
        reportWriter.writeReport(analysisContext, results)
        def reportFile = new File('CodeNarcCodeClimateReport.json')
        def jsonAsString = reportFile.text
        reportFile.delete()      // comment out to keep report file around for easy inspection
        assertJson(jsonAsString, REPORT_JSON)
    }

    @Test
    void testWriteReport_WritesToConfiguredReportFile() {
        reportWriter.writeToStandardOut = false
        reportWriter.outputFile = NEW_REPORT_FILE
        reportWriter.writeReport(analysisContext, results)
        def reportFile = new File(NEW_REPORT_FILE)
        def jsonAsString = reportFile.text
        reportFile.delete()
        assertJson(jsonAsString, REPORT_JSON)
    }

    @Test
    void testWriteReport_NullResults() {
        TestUtil.shouldFailWithMessageContaining('results') { reportWriter.writeReport(analysisContext, null) }
    }

    @Test
    void testWriteReport_NullAnalysisContext() {
        TestUtil.shouldFailWithMessageContaining('analysisContext') { reportWriter.writeReport(null, results) }
    }

    @Test
    void testDefaultOutputFile_CodeNarcCodeClimateReport() {
        assert reportWriter.defaultOutputFile == 'CodeNarcCodeClimateReport.json'
    }

    //--------------------------------------------------------------------------
    // Setup and helper methods
    //--------------------------------------------------------------------------

    @Before
    void setUpCodeClimateReportWriterTest() {
        reportWriter = new CodeClimateReportWriter()

        def srcMainDirResults = new DirectoryResults('src/main', 1)
        srcMainDaoDirResults = new DirectoryResults('src/main/dao', 2)
        def srcTestDirResults = new DirectoryResults('src/test', 3)
        def srcMainFileResults1 = new FileResults('src/main/MyAction.groovy', [VIOLATION1, VIOLATION3, VIOLATION3, VIOLATION1, VIOLATION2])
        def fileResultsMainDao1 = new FileResults('src/main/dao/MyDao.groovy', [VIOLATION3])
        def fileResultsMainDao2 = new FileResults('src/main/dao/MyOtherDao.groovy', [VIOLATION2])

        srcMainDirResults.addChild(srcMainFileResults1)
        srcMainDirResults.addChild(srcMainDaoDirResults)
        srcMainDaoDirResults.addChild(fileResultsMainDao1)
        srcMainDaoDirResults.addChild(fileResultsMainDao2)

        results = new DirectoryResults()
        results.addChild(srcMainDirResults)
        results.addChild(srcTestDirResults)

        ruleSet = new ListRuleSet([     // NOT in alphabetical order
                                        new DuplicateImportRule(description:'Custom: Duplicate imports'),
                                        new UnnecessaryBooleanInstantiationRule()
        ])
        analysisContext = new AnalysisContext(sourceDirectories:[SRC_DIR1, SRC_DIR2], ruleSet:ruleSet)
        stringWriter = new StringWriter()
    }
}
