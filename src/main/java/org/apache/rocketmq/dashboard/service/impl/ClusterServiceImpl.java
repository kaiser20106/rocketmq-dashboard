/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.rocketmq.dashboard.service.impl;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.apache.rocketmq.common.attribute.TopicMessageType;
import org.apache.rocketmq.dashboard.service.ClusterService;
import org.apache.rocketmq.dashboard.util.JsonUtil;
import org.apache.rocketmq.remoting.protocol.body.ClusterInfo;
import org.apache.rocketmq.remoting.protocol.body.KVTable;
import org.apache.rocketmq.remoting.protocol.route.BrokerData;
import org.apache.rocketmq.tools.admin.MQAdminExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class ClusterServiceImpl implements ClusterService {
    private Logger logger = LoggerFactory.getLogger(ClusterServiceImpl.class);
    @Resource
    private MQAdminExt mqAdminExt;

    @Override
    public Map<String, Object> list() {
        try {
            Map<String, Object> resultMap = Maps.newHashMap();
            ClusterInfo clusterInfo = mqAdminExt.examineBrokerClusterInfo();
            logger.info("op=look_clusterInfo {}", JsonUtil.obj2String(clusterInfo));
            Map<String/*brokerName*/, Map<Long/* brokerId */, Object/* brokerDetail */>> brokerServer = Maps.newHashMap();
            for (BrokerData brokerData : clusterInfo.getBrokerAddrTable().values()) {
                Map<Long, Object> brokerMasterSlaveMap = Maps.newHashMap();
                for (Map.Entry<Long/* brokerId */, String/* broker address */> brokerAddr : brokerData.getBrokerAddrs().entrySet()) {
                    KVTable kvTable = mqAdminExt.fetchBrokerRuntimeStats(brokerAddr.getValue());
                    brokerMasterSlaveMap.put(brokerAddr.getKey(), kvTable.getTable());
                }
                brokerServer.put(brokerData.getBrokerName(), brokerMasterSlaveMap);
            }
            resultMap.put("clusterInfo", clusterInfo);
            resultMap.put("brokerServer", brokerServer);
            // add messageType
            resultMap.put("messageTypes", Arrays.stream(TopicMessageType.values()).sorted()
                    .collect(Collectors.toMap(TopicMessageType::getValue, messageType -> String.format("MESSAGE_TYPE_%s", messageType.getValue()))));
            return resultMap;
        } catch (Exception err) {
            Throwables.throwIfUnchecked(err);
            throw new RuntimeException(err);
        }
    }


    @Override
    public Properties getBrokerConfig(String brokerAddr) {
        Properties properties = null;
        try {
            properties = mqAdminExt.getBrokerConfig(brokerAddr);
            if (properties == null) {
                throw new RuntimeException("get broker config failed, brokerAddr:" + brokerAddr);
            }
        } catch (Exception e) {
            Throwables.throwIfUnchecked(e);
            throw new RuntimeException(e);
        }
        return properties;
    }
}
