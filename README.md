# How to build PSI Probe [![Build Status](https://travis-ci.org/psi-probe/psi-probe.svg?branch=master)](https://travis-ci.org/psi-probe/psi-probe)

1.	**Clone PSI Probe's git repository.**

	*Note: If you plan to contribute to PSI Probe, you should create your own fork on GitHub first and clone that.  Otherwise, follow these steps to build the latest version of PSI Probe for yourself.*

	Execute the following command:

		git clone https://github.com/psi-probe/psi-probe

	This will create directory called `psi-probe`. Subsequent steps will refer to this as "your PSI Probe base directory."

2.	**Download and install Maven 3.**

	You may download it from the [Apache Maven website](http://maven.apache.org/download.cgi).

3.	**Run Maven.**

	Execute the following command from your PSI Probe base directory:

		mvn package

	This will create a deployable file at `web/target/probe.war`.
