import static org.junit.Assert.*
import org.junit.*

class ConfigTest {

  @Test
  public void defaultRuleSet() {
    //codenarc provides its own basic defaults
    def config = new Config([configFile: "/usr/src/app/fixtures/default/config.json", codeFolder: "/usr/src/app/fixtures/default"])
    assertEquals "", config.ruleSet()
  }

  @Test
  public void honorDefaultRulesIfPresent() { //if the default rules file is there add it to the rules path

    def config = new Config([configFile: "fixtures/ruleset_default_file/config.json", codeFolder: "fixtures/ruleset_default_file"])
    assertEquals "file:fixtures/ruleset_default_file/ruleset.xml", config.ruleSet()
  }

  @Test
  public void specifiedRuleSetFile() {
    def config = new Config([configFile: "fixtures/specified_file/config.new.json", codeFolder: "fixtures/specified_file"])
    assertEquals "file:fixtures/specified_file/ruleset.xml", config.ruleSet() //TODO:figure out how this doesn't need to be classpath file:
  }

}
