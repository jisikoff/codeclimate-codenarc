import groovy.json.JsonSlurper
import groovy.util.FileNameFinder

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
        if(parsedConfig?.config?.file) {
            def files = [parsedConfig?.config?.file] //TODO: just support one for now
            files?.each { checkFileExists(it) }
            return files?.join(",")
        }
    }

    private def pathsToInclude() {
        return parsedConfig.include_paths?.collect { appContext.codeFolder + File.pathSeparator + it }?.join(",")
    }

    private def pathsToExclude() {
        return parsedConfig.exclude_paths?.collect { appContext.codeFolder + File.pathSeparator + it }?.join(",")
    }

    private checkFileExists(String configFile) {
        def rules = new File(appContext.codeFolder, configFile)
        if (rules.exists()) {
            return rules.absolutePath
        } else {
            System.err.println "Config file ${configFile} not found"
            System.exit(1)
        }
    }
}
