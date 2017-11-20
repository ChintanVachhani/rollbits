/**
 * Copyright 2016 Gash.
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
package gash.router.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import gash.router.server.communication.ExternalCommServer;
import gash.router.server.communication.HeartbeatServer;
import gash.router.server.communication.InternalCommServer;
import gash.router.server.discovery.InternalDiscoveryClient;
import gash.router.server.discovery.InternalDiscoveryServer;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gash.router.container.RoutingConf;
import io.netty.bootstrap.ServerBootstrap;

import static java.lang.Thread.sleep;

public class Server {
    protected static Logger logger = LoggerFactory.getLogger("server");

    public static HashMap<Integer, ServerBootstrap> bootstrap = new HashMap<Integer, ServerBootstrap>();

    public static final String sPort = "port";
    public static final String sPoolSize = "pool.size";

    protected RoutingConf conf;
    protected boolean background = false;

    public Server(RoutingConf conf) {
        this.conf = conf;
    }

    public void release() {
    }

    public void startServer() {
        // network discovery mechanism using UDP
        // listening for discovery request by other servers
        InternalDiscoveryServer internalDiscoveryServer = new InternalDiscoveryServer(conf);
        Thread dsthread = new Thread(internalDiscoveryServer);
        dsthread.start();

        // finding all the active servers
        InternalDiscoveryClient internalDiscoveryClient = new InternalDiscoveryClient(conf);
        Thread dcthread = new Thread(internalDiscoveryClient);
        dcthread.start();

        // start heartbeat server
        HeartbeatServer heartbeatServer = new HeartbeatServer(conf);
        Thread hbthread = new Thread(heartbeatServer);
        hbthread.start();

        // start internal communication over channel
        InternalCommServer comm = new InternalCommServer(conf);

        //ExternalCommServer comm = new ExternalCommServer(conf);

        if (background) {
            Thread cthread = new Thread(comm);
            cthread.start();
        } else
            comm.run();
    }

    /**
     * static because we need to get a handle to the factory from the shutdown
     * resource
     */
    public static void shutdown() {
        logger.info("Server shutdown");
        System.exit(0);
    }

    /**
     * initialize the server with a configuration of it's resources
     *
     * @param cfg
     */
    public Server(File cfg) {
        init(cfg);
    }

    private void init(File cfg) {
        if (!cfg.exists())
            throw new RuntimeException(cfg.getAbsolutePath() + " not found");
        // resource initialization - how message are processed
        BufferedInputStream br = null;
        try {
            byte[] raw = new byte[(int) cfg.length()];
            br = new BufferedInputStream(new FileInputStream(cfg));
            br.read(raw);
            conf = JsonUtil.decode(new String(raw), RoutingConf.class);
            if (!verifyConf(conf))
                throw new RuntimeException("verification of configuration failed");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean verifyConf(RoutingConf conf) {
        return (conf != null);
    }

    /**
     * help with processing the configuration information
     *
     * @author gash
     */
    public static class JsonUtil {
        private static JsonUtil instance;

        public static void init(File cfg) {

        }

        public static JsonUtil getInstance() {
            if (instance == null)
                throw new RuntimeException("Server has not been initialized");

            return instance;
        }

        public static String encode(Object data) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString(data);
            } catch (Exception ex) {
                return null;
            }
        }

        public static <T> T decode(String data, Class<T> theClass) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(data.getBytes(), theClass);
            } catch (Exception ex) {
                return null;
            }
        }
    }

}
