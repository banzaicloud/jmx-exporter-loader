# JMX exporter loader

This application loads a [Prometheus JMX Exporter](https://github.com/prometheus/jmx_exporter) agent into running Java processes.
[Prometheus JMX Exporter](https://github.com/prometheus/jmx_exporter) as it is implemented can be loaded into Java processes only at JVM startup. With a small change it can be made loadable into an already running Java process. You can take a look at the change in our [jmx_exporter fork](https://github.com/banzaicloud/jmx_exporter/commit/e83a7f123a983402aac2d831a716da4f4cd1ed5d)

The loader expects the following configuration to be passed as system properties:

* **pid** - pid of the java process to inject prometheus java agent into
* **prometheus.javaagent.path** - path to the prometheus java agent jar
* **prometheus.javaagent.configPath** - path to the config file of prometheus java agent (example [config file](https://github.com/prometheus/jmx_exporter#configuration))
* **prometheus.port** - the port metrics can be scraped from
