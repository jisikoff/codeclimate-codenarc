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
            return files?.collect { "file:" + appContext.codeFolder + File.separator + it }?.join(",")
        }
    }

//    private def pathsToInclude() {
//        return parsedConfig.include_paths?.collect { appContext.codeFolder + File.separator + it }?.join(",")
//    }

    private def pathsToExclude() {
        return parsedConfig.exclude_paths?.collect { appContext.codeFolder + File.separator + it }?.join(",")
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

    private def pathsToInclude() {
        def includePaths = parsedConfig.include_paths?.join(" ")
        def codeFolder = new File(appContext.codeFolder)

        def files = new FileNameFinder().getFileNames(appContext.codeFolder, includePaths)

        def i = files.iterator()
        while(i.hasNext()) {
            def name = i.next()
            if(!name.endsWith(".groovy")) {
                i.remove()
            }
        }

        def fileNames = files.toString()
        fileNames.substring(1, fileNames.length()-1).replaceAll("\\s+","")
    }
}
