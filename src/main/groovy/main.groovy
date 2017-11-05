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
        execute("pwd")
        execute("ls")
        def config = new Config(appContext(args))
        def cmd = "java -Dorg.slf4j.simpleLogger.defaultLogLevel=off -classpath lib/groovy-all-2.4.12.jar:lib/CodeNarc-1.0.jar:lib/slf4j-api-1.7.25.jar:lib/slf4j-simple-1.7.25.jar org.codenarc.CodeNarc -basedir=${config.appContext.codeFolder} -rulesetfiles=${config.ruleSet()} -includes=${config.pathsToAnalyze} -excludes=${config.pathsToExclude} -report=codeclimate"
        println("Executing: ${cmd}")
        execute(cmd)
     }
}
