import groovy.json.JsonSlurper
import groovy.util.FileNameFinder

import java.nio.file.Paths

class Config {
    static String DEFAULT_RULES = "java-basic"
    def args
    def appContext
    def parsedConfig
    def pathsToAnalyze
    def pathsToExclude

    Config(appContext) {
        this.appContext = appContext
        this.parsedConfig = new JsonSlurper().parse(new File(appContext.configFile), "UTF-8")
        this.pathsToAnalyze = pathsToInclude()
        this.pathsToExclude = pathsToExclude()
    }


    def ruleSet() {
        def rulesets = []

        //add the default if exists
        def defaultFileName = "ruleset.xml"
        def defaultFile = new File(appContext.codeFolder, defaultFileName)
        if(defaultFile.exists())
            rulesets.add("file:" + appContext.codeFolder + File.separator + defaultFileName)

        //add the the other rulesets
        def config = parsedConfig.config
        def filepath = null

        switch(config) {
            case String:
                filepath = config
                break
            case Map:
                filepath = config.file
                break
        }

        if(filepath) {
            def files = [filepath] //TODO: just support one for now
            files?.each { checkFileExists(it) }
            rulesets.addAll(files?.collect { "file:" + appContext.codeFolder + File.separator + it })
        }
        return rulesets.toSet()?.join(",")
    }

//    private def pathsToInclude() {
//        return parsedConfig.include_paths?.collect { appContext.codeFolder + File.separator + it }?.join(",")
//    }

    private def pathsToExclude() {
        return parsedConfig.exclude_paths?.collect { Paths.get(appContext.codeFolder, it) }?.join(",")
    }

    //if you specified the rules they should exist
    private checkFileExists(String configFile) {
        def rules = new File(appContext.codeFolder, configFile)
        if (rules.exists()) {
            return rules.absolutePath
        } else {
            System.err.println "Config file ${configFile} not found"
            System.exit(1)
        }
    }

    private def pathsToInclude() {
        def directories = parsedConfig.include_paths?.findAll {it.endsWith(File.separator) }?.collect { it + "**.groovy"}
        def files = parsedConfig.include_paths?.findAll {it.endsWith(".groovy")}
        (directories + files)?.collect { Paths.get(appContext.codeFolder, it.toString()) }?.join(",")
    }
}
