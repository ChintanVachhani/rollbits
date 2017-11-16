/**
 * Copyright 2012 Gash.
 * <p>
 * This file and intellectual content is protected under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package gash.router.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Routing information for the server - internal use only
 *
 * @author gash
 */
@XmlRootElement(name = "conf")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoutingConf {
    private int internalCommunicationPort;
    private int externalCommunicationPort;
    private int internalDiscoveryPort;
    private int externalDiscoveryPort;
    private String nodeBroadcastAddress;
    private String nodeAddress;
    private String nodeId;
    private String groupTag;
    private String secret;
    private List<RoutingEntry> routing;

    public HashMap<String, String> asHashMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        if (routing != null) {
            for (RoutingEntry entry : routing) {
                map.put(entry.path, entry.clazz);
            }
        }
        return map;
    }

    public void addEntry(RoutingEntry entry) {
        if (entry == null)
            return;

        if (routing == null)
            routing = new ArrayList<RoutingEntry>();

        routing.add(entry);
    }

    public int getInternalCommunicationPort() {
        return internalCommunicationPort;
    }

    public void setInternalCommunicationPort(int internalCommunicationPort) {
        this.internalCommunicationPort = internalCommunicationPort;
    }

    public int getExternalCommunicationPort() {
        return externalCommunicationPort;
    }

    public void setExternalCommunicationPort(int externalCommunicationPort) {
        this.externalCommunicationPort = externalCommunicationPort;
    }

    public int getInternalDiscoveryPort() {
        return internalDiscoveryPort;
    }

    public void setInternalDiscoveryPort(int internalDiscoveryPort) {
        this.internalDiscoveryPort = internalDiscoveryPort;
    }

    public int getExternalDiscoveryPort() {
        return externalDiscoveryPort;
    }

    public void setExternalDiscoveryPort(int externalDiscoveryPort) {
        this.externalDiscoveryPort = externalDiscoveryPort;
    }

    public String getNodeBroadcastAddress() {
        return nodeBroadcastAddress;
    }

    public void setNodeBroadcastAddress(String nodeBroadcastAddress) {
        this.nodeBroadcastAddress = nodeBroadcastAddress;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getGroupTag() {
        return groupTag;
    }

    public void setGroupTag(String groupTag) {
        this.groupTag = groupTag;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public List<RoutingEntry> getRouting() {
        return routing;
    }

    public void setRouting(List<RoutingEntry> conf) {
        this.routing = conf;
    }

    @XmlRootElement(name = "entry")
    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static final class RoutingEntry {
        public RoutingEntry() {
        }

        public RoutingEntry(String path, String clazz) {
            this.path = path;
            this.clazz = clazz;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getClazz() {
            return clazz;
        }

        public void setClazz(String clazz) {
            this.clazz = clazz;
        }

        private String path;
        private String clazz;
    }
}
