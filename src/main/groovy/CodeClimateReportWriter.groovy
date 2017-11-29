/*
 * Copyright 2009 the original author or authors.
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


import groovy.json.JsonBuilder
import org.codenarc.AnalysisContext
import org.codenarc.report.AbstractReportWriter
import org.codenarc.results.Results
import org.codenarc.rule.Violation

/**
 * ReportWriter that generates output in CodeClimate format.
 */
class CodeClimateReportWriter extends AbstractReportWriter {

    String title
    String defaultOutputFile = 'CodeNarcCodeClimateReport.json'

    CodeClimateReportWriter(){
        writeToStandardOut = true;
    }

    void writeReport(Writer writer, AnalysisContext analysisContext, Results results) {
        assert analysisContext
        assert results

        initializeResourceBundle()

        def resultsFlat = flattenResults(results, [])
        def list = resultsFlat.findAll { it.isFile() }.collectMany { result ->
            writeViolations(result)
        }

        def builder = new JsonBuilder()
        list.each { item ->
            if (item) {
                builder.call(item)
                writer.append(builder.toString() + '\0' + '\n')
                writer.flush()
            }
        }
        writer.flush()
    }

    protected List<Results> flattenResults(Results results, List<Results> list) {
        list.add(results)
        results.children.each { child ->
            if (child.isFile()) {
                list.add(child)
            } else {
                flattenResults(child, list)
            }
        }
        return list
    }

    protected writeViolations(Results results) {
        results.violations.collect { violation ->
           writeViolation(violation, results.path)
        }
    }

    protected writeViolation(Violation violation, String path) {
        return [
                type       : 'issue',
                check_name : "${violation?.rule?.name}",
                description: violation?.message ? "${violation?.message}" : "${getDescriptionForRule(violation?.rule)}",
                content    : [body: "${getDescriptionForRule(violation?.rule)}"],
                categories : RuleData.rules.get(violation?.rule?.name)?.categories ? RuleData.rules.get(violation?.rule?.name)?.categories : ['Style'],
                location   : [
                        path : path,
                        lines: [
                                begin: violation?.lineNumber ? violation?.lineNumber : 0,
                                end  : violation?.lineNumber ? violation?.lineNumber : 0,
                        ]
                ],
                remediation_points: RuleData.rules.get(violation?.rule?.name)?.points ? RuleData.rules.get(violation?.rule?.name)?.points : 50000,
                severity   : getSeverity(violation?.rule?.priority)
        ]
    }

    protected String getSeverity(int priority) {
        def result = 'info'
        switch (priority) {
            case 1:
                result = 'critical'
                break
            case 2:
                result = 'major'
                break
            case 3:
                result = 'minor'
                break
        }
        return result
    }

}
