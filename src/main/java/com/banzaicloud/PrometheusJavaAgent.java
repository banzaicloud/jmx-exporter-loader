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

import java.io.IOException;

public class PrometheusJavaAgent {
    String prometheusAgentPath;
    String prometheusAgentConfigPath;
    int prometheusPort;

    private PrometheusJavaAgent() {}

    /**
     * Constructor.
     * @param prometheusAgentPath port metrics can be scraped from
     * @param prometheusAgentConfigPath path to the config file of prometheus java agent
     * @param prometheusPort path to the prometheus java agent jar
     */
    public PrometheusJavaAgent(String prometheusAgentPath, String prometheusAgentConfigPath, int prometheusPort) {
        this.prometheusAgentPath = prometheusAgentPath;
        this.prometheusAgentConfigPath = prometheusAgentConfigPath;
        this.prometheusPort = prometheusPort;
    }

    public boolean loadInto(VirtualMachineDescriptor vmd) {
        VirtualMachine vm = attach(vmd);
        if (vm == null)
        {
            return false;
        }

        try {
            vm.loadAgent(
                    prometheusAgentPath,
                    String.format("%d:%s", prometheusPort, prometheusAgentConfigPath)
                    );

            return true;

        } catch (AgentLoadException e) {
            System.out.println(e);
        } catch (AgentInitializationException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
        finally {
            detach(vm);
        }

        return false;
    }

    private VirtualMachine attach(VirtualMachineDescriptor vmd) {
        VirtualMachine vm = null;
        try {
            vm = VirtualMachine.attach(vmd);
        }
        catch (AttachNotSupportedException ex) {
            System.out.println("Attaching to (" + vmd + ") failed due to: " + ex);
        }
        catch (IOException ex) {
            System.out.println("Attaching to (" + vmd + ") failed due to: " + ex);
        }

        return vm;
    }


    private void detach(VirtualMachine vm) {
        if (vm != null) {
            try {
                vm.detach();
            } catch (IOException e) {
                System.out.println("Detaching from (" + vm + ") failed due to: " + e);
            }
        }
    }

}
