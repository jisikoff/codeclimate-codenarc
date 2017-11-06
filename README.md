# Code Climate CodeNarc (Groovy) Engine
`codeclimate-codenarc` is a Code Climate engine for Groovy that wraps the [CodeNarc](https://github.com/CodeNarc/CodeNarc) static analysis tool. You can run it on your command line using the Code Climate CLI.

##WORK IN PROGRESS

- only will run locally in dev mode after building locally from docker file
- all issues are categorized as "Style" right now until I can go through and manually 
categorize the hundreds of various rules
- need to go through the qa process mentioned here: (https://docs.codeclimate.com/v1.0/docs/building-a-code-climate-engine)

### Installation

1. If you haven't already, [install the Code Climate CLI](https://github.com/codeclimate/codeclimate).
2. clone the repo and run `make test` to build the image locally and test it
3. Add a `.codeclimate.yml` to your project folder that enables the experimental codenarc engine (see below)
4. You're ready to analyze! Browse into your project's folder and run `CODECLIMATE_DEBUG=1 codeclimate analyze  --dev   `.
5. To add you custom codenarc rules, open `.codeclimate.yml` and add a `config` entry pointing to your `ruleset.xml` file:


```yml
engines:
  codenarc:
    enabled: true
    config: ruleset.xml
```

### Need help?

For help with CodeNarc, [check out their documentation](http://codenarc.sourceforge.net/codenarc-command-line.html).

[cc-docs-codenarc]: https://docs.codeclimate.com/docs/codenarc
