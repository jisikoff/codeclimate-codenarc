import groovy.util.*

class Main {
    static def execute(command) {
        ProcessBuilder builder = new ProcessBuilder(command.split(' '))
        def env = builder.environment()
        env.put("JAVA_OPTS", "-XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=30")
        Process process = builder.start()
        process.consumeProcessOutput(System.out, System.err)
        process.waitFor()
        System.exit(process.exitValue())
    }

    static def appContext(args) {
        def cli = new CliBuilder(usage: "${this.class.name}")
        cli._(longOpt: "configFile", required: true, args: 1, "Path to config.json file")
        cli._(longOpt: "codeFolder", required: true, args: 1, "Path to code folder")
        cli.parse(args)
    }

    public static void main(args) {
        def config = new Config(appContext(args))
        def includesString = config.pathsToAnalyze ? "-includes=${config.pathsToAnalyze}" : "-includes=**.groovy"
        def excludesString = config.pathsToExclude ? "-excludes=${config.pathsToExclude}" : ""
        def rulesetString = config.ruleSet() ? "-rulesetfiles=${config.ruleSet()}" : ""

        //good command with hardcoded classpath TODO:make more flexible with directory classpath
        def cmd = "java -Dorg.slf4j.simpleLogger.defaultLogLevel=off -classpath /usr/src/app/lib/groovy-all-2.4.12.jar:/usr/src/app/lib/codeclimate-codenarc-1.0.jar:/usr/src/app/lib/CodeNarc-1.0.jar:/usr/src/app/lib/GMetrics-1.0.jar:/usr/src/app/lib/slf4j-api-1.7.25.jar:/usr/src/app/lib/slf4j-simple-1.7.25.jar org.codenarc.CodeNarc -basedir=${config.appContext.codeFolder} ${rulesetString} ${includesString} ${excludesString} -report=CodeClimateReportWriter".replaceAll(/\s\s+/, ' ')

        //minimal for testing:
        //def cmd = "java -Dorg.slf4j.simpleLogger.defaultLogLevel=off -classpath /usr/src/app/lib/groovy-all-2.4.12.jar:/usr/src/app/lib/CodeNarc-1.0.jar:/usr/src/app/lib/GMetrics-1.0.jar:/usr/src/app/lib/slf4j-api-1.7.25.jar:/usr/src/app/lib/slf4j-simple-1.7.25.jar org.codenarc.CodeNarc"

        //Log the command out
        def printErr = System.err.&println
        printErr(cmd)

        execute(cmd)
     }
}
