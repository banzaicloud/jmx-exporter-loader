/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.banzaicloud;



import com.sun.tools.attach.*;

import java.util.List;
import java.util.stream.Collectors;

public class JmxExporterLoader {

    public static void main(String[] args) {
        // the pid of the java process to inject prometheus java agent into
        long pid = Long.parseLong(System.getProperty("pid"));

        // path to the prometheus java agent jar
        String prometheusAgentPath = System.getProperty("prometheus.javaagent.path");

        // the port metrics can be scraped from
        int port = Integer.parseInt(System.getProperty("prometheus.port"));

        // path to the config file of prometheus java agent
        String prometheusAgentConfigFile = System.getProperty("prometheus.javaagent.configPath");


        if (pid == myPid()) {
            System.out.println("The provided `pid` must be different from the pid of the current process !");
            System.exit(1);
        }

         VirtualMachineDescriptor vmd = findVirtualMachine(pid);


        PrometheusJavaAgent agent  = new PrometheusJavaAgent(prometheusAgentPath, prometheusAgentConfigFile, port);

        if (agent.loadInto(vmd) == false) {
            System.exit(1);
        }

        System.exit(0);
    }


    private static VirtualMachineDescriptor findVirtualMachine(long pid) {
        List<VirtualMachineDescriptor> vmds = VirtualMachine.list();

        List<VirtualMachineDescriptor> selectedVmds = vmds.stream()
                .filter(javaProc -> javaProc.id().equalsIgnoreCase(Long.toString(pid)))
                .collect(Collectors.toList());

        if (selectedVmds.isEmpty()) {
            System.out.println(String.format("No Java process with PID %d found !", pid));
            System.exit(1);
        }

        return selectedVmds.get(0);
    }


    private static long myPid() {
        return ProcessHandle.current().pid();
    }
}
