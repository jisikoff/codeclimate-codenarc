# Code Climate CodeNarc Engine

`codeclimate-pmd` is a Code Climate engine that wraps the [CodeNarc](https://github.com/CodeNarc/CodeNarc) static analysis tool. You can run it on your command line using the Code Climate CLI, or on our hosted analysis platform.

### Installation

1. If you haven't already, [install the Code Climate CLI](https://github.com/codeclimate/codeclimate).
2. Run `codeclimate engines:enable pmd`. This command both installs the engine and enables it in your `.codeclimate.yml` file.
3. You're ready to analyze! Browse into your project's folder and run `codeclimate analyze`.
4. To add you custom codenarc rules, open `.codeclimate.yml` and add a `config` entry pointing to your `ruleset.xml` file:
```yml
engines:
  pmd:
    enabled: true
    config: ruleset.xml
```

### Need help?

For help with CodeNarc, [check out their documentation](http://codenarc.sourceforge.net/codenarc-command-line.html).

[cc-docs-codenarc]: https://docs.codeclimate.com/docs/codenarc
