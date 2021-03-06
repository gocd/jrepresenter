# JRepresenter — Annotation based JSON serializers for GoCD API 

## Running the code

`./gradlew clean jar test`

## Publishing to maven central

> **Note:** On the build systems, these properties are setup in `$HOME/.gradle/gradle.properties` using docker swarm secrets.

* build the jars and create a staging repository
  `./gradlew clean signArchives uploadArchives -Psigning.keyId=8816C449 -Psigning.password=... -Psigning.secretKeyRingFile=$HOME/.gnupg/secring.gpg -PnexusUsername=... -PnexusPassword=...`
  
* close and release the staging repository
  `./gradlew closeAndReleaseRepository -Psigning.keyId=8816C449 -Psigning.password=... -Psigning.secretKeyRingFile=$HOME/.gnupg/secring.gpg -PnexusUsername=... -PnexusPassword=...`

## Examples

See examples in the examples directory.

## Contributing

We'd love it if you contributed to GoCD. For information on contributing to this project, please see our [contributor's guide](https://gocd.org/contribute).
A lot of useful information like links to user documentation, design documentation, mailing lists etc. can be found in the [resources](https://gocd.org/community/resources.html) section.

## License

GoCD is an open source project, sponsored by [ThoughtWorks Inc.](https://www.thoughtworks.com) under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
