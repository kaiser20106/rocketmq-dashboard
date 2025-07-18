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
package org.apache.rocketmq.dashboard.service.client;

import com.google.common.base.Throwables;
import org.apache.rocketmq.client.QueryResult;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.MQAdminImpl;
import org.apache.rocketmq.common.CheckRocksdbCqWriteResult;
import org.apache.rocketmq.common.Pair;
import org.apache.rocketmq.common.TopicConfig;
import org.apache.rocketmq.common.message.MessageClientIDSetter;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.message.MessageRequestMode;
import org.apache.rocketmq.dashboard.util.JsonUtil;
import org.apache.rocketmq.remoting.RemotingClient;
import org.apache.rocketmq.remoting.exception.RemotingCommandException;
import org.apache.rocketmq.remoting.exception.RemotingConnectException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.remoting.exception.RemotingSendRequestException;
import org.apache.rocketmq.remoting.exception.RemotingTimeoutException;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;
import org.apache.rocketmq.remoting.protocol.RequestCode;
import org.apache.rocketmq.remoting.protocol.ResponseCode;
import org.apache.rocketmq.remoting.protocol.admin.ConsumeStats;
import org.apache.rocketmq.remoting.protocol.admin.RollbackStats;
import org.apache.rocketmq.remoting.protocol.admin.TopicStatsTable;
import org.apache.rocketmq.remoting.protocol.body.AclInfo;
import org.apache.rocketmq.remoting.protocol.body.BrokerMemberGroup;
import org.apache.rocketmq.remoting.protocol.body.BrokerReplicasInfo;
import org.apache.rocketmq.remoting.protocol.body.BrokerStatsData;
import org.apache.rocketmq.remoting.protocol.body.ClusterInfo;
import org.apache.rocketmq.remoting.protocol.body.ConsumeMessageDirectlyResult;
import org.apache.rocketmq.remoting.protocol.body.ConsumeStatsList;
import org.apache.rocketmq.remoting.protocol.body.ConsumerConnection;
import org.apache.rocketmq.remoting.protocol.body.ConsumerRunningInfo;
import org.apache.rocketmq.remoting.protocol.body.EpochEntryCache;
import org.apache.rocketmq.remoting.protocol.body.GroupList;
import org.apache.rocketmq.remoting.protocol.body.HARuntimeInfo;
import org.apache.rocketmq.remoting.protocol.body.KVTable;
import org.apache.rocketmq.remoting.protocol.body.ProducerConnection;
import org.apache.rocketmq.remoting.protocol.body.ProducerTableInfo;
import org.apache.rocketmq.remoting.protocol.body.QueryConsumeQueueResponseBody;
import org.apache.rocketmq.remoting.protocol.body.QueueTimeSpan;
import org.apache.rocketmq.remoting.protocol.body.SubscriptionGroupWrapper;
import org.apache.rocketmq.remoting.protocol.body.TopicConfigSerializeWrapper;
import org.apache.rocketmq.remoting.protocol.body.TopicList;
import org.apache.rocketmq.remoting.protocol.body.UserInfo;
import org.apache.rocketmq.remoting.protocol.header.ExportRocksDBConfigToJsonRequestHeader;
import org.apache.rocketmq.remoting.protocol.header.GetTopicConfigRequestHeader;
import org.apache.rocketmq.remoting.protocol.header.controller.ElectMasterResponseHeader;
import org.apache.rocketmq.remoting.protocol.header.controller.GetMetaDataResponseHeader;
import org.apache.rocketmq.remoting.protocol.heartbeat.SubscriptionData;
import org.apache.rocketmq.remoting.protocol.route.TopicRouteData;
import org.apache.rocketmq.remoting.protocol.statictopic.TopicConfigAndQueueMapping;
import org.apache.rocketmq.remoting.protocol.statictopic.TopicQueueMappingDetail;
import org.apache.rocketmq.remoting.protocol.subscription.GroupForbidden;
import org.apache.rocketmq.remoting.protocol.subscription.SubscriptionGroupConfig;
import org.apache.rocketmq.tools.admin.MQAdminExt;
import org.apache.rocketmq.tools.admin.api.BrokerOperatorResult;
import org.apache.rocketmq.tools.admin.api.MessageTrack;
import org.apache.rocketmq.tools.admin.common.AdminToolResult;
import org.joor.Reflect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.apache.rocketmq.remoting.protocol.RemotingSerializable.decode;

@Service
public class MQAdminExtImpl implements MQAdminExt {
    private Logger logger = LoggerFactory.getLogger(MQAdminExtImpl.class);


    public MQAdminExtImpl() {
    }


    @Override
    public void updateBrokerConfig(String brokerAddr, Properties properties)
            throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException,
            UnsupportedEncodingException, InterruptedException, MQBrokerException, MQClientException {
        MQAdminInstance.threadLocalMQAdminExt().updateBrokerConfig(brokerAddr, properties);
    }

    @Override
    public void createAndUpdateTopicConfig(String addr, TopicConfig config)
            throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        MQAdminInstance.threadLocalMQAdminExt().createAndUpdateTopicConfig(addr, config);
    }

    @Override
    public void createAndUpdateTopicConfigList(String s, List<TopicConfig> list) throws InterruptedException, RemotingException, MQClientException {

    }


    @Override
    public void createAndUpdateSubscriptionGroupConfig(String addr, SubscriptionGroupConfig config)
            throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        MQAdminInstance.threadLocalMQAdminExt().createAndUpdateSubscriptionGroupConfig(addr, config);
    }

    @Override
    public void createAndUpdateSubscriptionGroupConfigList(String s, List<SubscriptionGroupConfig> list) throws RemotingException, MQBrokerException, InterruptedException, MQClientException {

    }

    @Override
    public SubscriptionGroupConfig examineSubscriptionGroupConfig(String addr, String group) throws MQBrokerException {
        RemotingClient remotingClient = MQAdminInstance.threadLocalRemotingClient();
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.GET_ALL_SUBSCRIPTIONGROUP_CONFIG, null);
        RemotingCommand response = null;
        try {
            response = remotingClient.invokeSync(addr, request, 8000);
        } catch (Exception err) {
            Throwables.throwIfUnchecked(err);
            throw new RuntimeException(err);
        }
        assert response != null;
        switch (response.getCode()) {
            case ResponseCode.SUCCESS: {
                SubscriptionGroupWrapper subscriptionGroupWrapper = decode(response.getBody(), SubscriptionGroupWrapper.class);
                return subscriptionGroupWrapper.getSubscriptionGroupTable().get(group);
            }
            default:
                throw new MQBrokerException(response.getCode(), response.getRemark());
        }
    }

    @Override
    public TopicConfig examineTopicConfig(String addr, String topic) throws MQBrokerException {
        RemotingClient remotingClient = MQAdminInstance.threadLocalRemotingClient();
        GetTopicConfigRequestHeader header = new GetTopicConfigRequestHeader();
        header.setTopic(topic);
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.GET_TOPIC_CONFIG, header);
        RemotingCommand response;
        try {
            response = remotingClient.invokeSync(addr, request, 3000);
        } catch (Exception err) {
            Throwables.throwIfUnchecked(err);
            throw new RuntimeException(err);
        }
        switch (response.getCode()) {
            case ResponseCode.SUCCESS: {
                TopicConfigAndQueueMapping topicConfigAndQueueMapping = decode(response.getBody(), TopicConfigAndQueueMapping.class);
                if (topicConfigAndQueueMapping == null) {
                    throw new MQBrokerException(ResponseCode.TOPIC_NOT_EXIST, "Topic not exist: " + topic);
                }
                return topicConfigAndQueueMapping;
            }
            default:
                throw new MQBrokerException(response.getCode(), response.getRemark());
        }
    }

    @Override
    public TopicStatsTable examineTopicStats(String topic)
            throws RemotingException, MQClientException, InterruptedException, MQBrokerException {
        return MQAdminInstance.threadLocalMQAdminExt().examineTopicStats(topic);
    }

    @Override
    public TopicList fetchAllTopicList() throws RemotingException, MQClientException, InterruptedException {
        TopicList topicList = MQAdminInstance.threadLocalMQAdminExt().fetchAllTopicList();
        logger.debug("op=look={}", JsonUtil.obj2String(topicList.getTopicList()));
        return topicList;
    }

    @Override
    public KVTable fetchBrokerRuntimeStats(String brokerAddr)
            throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException,
            InterruptedException, MQBrokerException {
        return MQAdminInstance.threadLocalMQAdminExt().fetchBrokerRuntimeStats(brokerAddr);
    }

    @Override
    public ConsumeStats examineConsumeStats(String consumerGroup)
            throws RemotingException, MQClientException, InterruptedException, MQBrokerException {
        return MQAdminInstance.threadLocalMQAdminExt().examineConsumeStats(consumerGroup);
    }

    @Override
    public CheckRocksdbCqWriteResult checkRocksdbCqWriteProgress(String s, String s1, long l) throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException, MQClientException {
        return null;
    }

    @Override
    public ConsumeStats examineConsumeStats(String consumerGroup, String topic)
            throws RemotingException, MQClientException, InterruptedException, MQBrokerException {
        return MQAdminInstance.threadLocalMQAdminExt().examineConsumeStats(consumerGroup, topic);
    }

    @Override
    public ConsumeStats examineConsumeStats(String s, String s1, String s2) throws RemotingException, MQClientException, InterruptedException, MQBrokerException {
        return null;
    }

    @Override
    public ClusterInfo examineBrokerClusterInfo()
            throws InterruptedException, MQBrokerException, RemotingTimeoutException, RemotingSendRequestException,
            RemotingConnectException {
        return MQAdminInstance.threadLocalMQAdminExt().examineBrokerClusterInfo();
    }

    @Override
    public TopicRouteData examineTopicRouteInfo(String topic)
            throws RemotingException, MQClientException, InterruptedException {
        return MQAdminInstance.threadLocalMQAdminExt().examineTopicRouteInfo(topic);
    }

    @Override
    public ConsumerConnection examineConsumerConnectionInfo(String consumerGroup)
            throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException,
            InterruptedException, MQBrokerException, RemotingException, MQClientException {
        return MQAdminInstance.threadLocalMQAdminExt().examineConsumerConnectionInfo(consumerGroup);
    }

    @Override
    public ProducerConnection examineProducerConnectionInfo(String producerGroup, String topic)
            throws RemotingException, MQClientException, InterruptedException, MQBrokerException {
        return MQAdminInstance.threadLocalMQAdminExt().examineProducerConnectionInfo(producerGroup, topic);
    }

    @Override
    public List<String> getNameServerAddressList() {
        return MQAdminInstance.threadLocalMQAdminExt().getNameServerAddressList();
    }

    @Override
    public int wipeWritePermOfBroker(String namesrvAddr, String brokerName)
            throws RemotingCommandException, RemotingConnectException, RemotingSendRequestException,
            RemotingTimeoutException, InterruptedException, MQClientException {
        return MQAdminInstance.threadLocalMQAdminExt().wipeWritePermOfBroker(namesrvAddr, brokerName);
    }

    @Override
    public int addWritePermOfBroker(String namesrvAddr,
                                    String brokerName) throws RemotingCommandException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, InterruptedException, MQClientException {
        return MQAdminInstance.threadLocalMQAdminExt().addWritePermOfBroker(namesrvAddr, brokerName);
    }

    @Override
    public void putKVConfig(String namespace, String key, String value) {
        MQAdminInstance.threadLocalMQAdminExt().putKVConfig(namespace, key, value);
    }

    @Override
    public String getKVConfig(String namespace, String key)
            throws RemotingException, MQClientException, InterruptedException {
        return MQAdminInstance.threadLocalMQAdminExt().getKVConfig(namespace, key);
    }

    @Override
    public KVTable getKVListByNamespace(String namespace)
            throws RemotingException, MQClientException, InterruptedException {
        return MQAdminInstance.threadLocalMQAdminExt().getKVListByNamespace(namespace);
    }

    @Override
    public void deleteTopicInBroker(Set<String> addrs, String topic)
            throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        logger.info("addrs={} topic={}", JsonUtil.obj2String(addrs), topic);
        MQAdminInstance.threadLocalMQAdminExt().deleteTopicInBroker(addrs, topic);
    }

    @Override
    public void deleteTopicInNameServer(Set<String> addrs, String topic)
            throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        MQAdminInstance.threadLocalMQAdminExt().deleteTopicInNameServer(addrs, topic);
    }

    @Override
    public void deleteSubscriptionGroup(String addr, String groupName)
            throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        MQAdminInstance.threadLocalMQAdminExt().deleteSubscriptionGroup(addr, groupName);
    }

    @Override
    public void deleteSubscriptionGroup(String addr, String groupName, boolean removeOffset)
            throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        MQAdminInstance.threadLocalMQAdminExt().deleteSubscriptionGroup(addr, groupName, removeOffset);
    }

    @Override
    public void createAndUpdateKvConfig(String namespace, String key, String value)
            throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        MQAdminInstance.threadLocalMQAdminExt().createAndUpdateKvConfig(namespace, key, value);
    }

    @Override
    public void deleteKvConfig(String namespace, String key)
            throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        MQAdminInstance.threadLocalMQAdminExt().deleteKvConfig(namespace, key);
    }

    @Override
    public List<RollbackStats> resetOffsetByTimestampOld(String consumerGroup, String topic, long timestamp,
                                                         boolean force) throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        return MQAdminInstance.threadLocalMQAdminExt().resetOffsetByTimestampOld(consumerGroup, topic, timestamp, force);
    }

    @Override
    public Map<MessageQueue, Long> resetOffsetByTimestamp(String topic, String group, long timestamp,
                                                          boolean isForce) throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        return MQAdminInstance.threadLocalMQAdminExt().resetOffsetByTimestamp(topic, group, timestamp, isForce);
    }

    @Override
    public Map<MessageQueue, Long> resetOffsetByTimestamp(String s, String s1, String s2, long l, boolean b) throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        return Map.of();
    }

    @Override
    public void resetOffsetNew(String consumerGroup, String topic, long timestamp)
            throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        MQAdminInstance.threadLocalMQAdminExt().resetOffsetNew(consumerGroup, topic, timestamp);
    }

    @Override
    public Map<String, Map<MessageQueue, Long>> getConsumeStatus(String topic, String group,
                                                                 String clientAddr) throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        return MQAdminInstance.threadLocalMQAdminExt().getConsumeStatus(topic, group, clientAddr);
    }

    @Override
    public void createOrUpdateOrderConf(String key, String value, boolean isCluster)
            throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        MQAdminInstance.threadLocalMQAdminExt().createOrUpdateOrderConf(key, value, isCluster);
    }

    @Override
    public GroupList queryTopicConsumeByWho(String topic)
            throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException,
            InterruptedException, MQBrokerException, RemotingException, MQClientException {
        return MQAdminInstance.threadLocalMQAdminExt().queryTopicConsumeByWho(topic);
    }

    @Override
    public boolean cleanExpiredConsumerQueue(String cluster)
            throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQClientException,
            InterruptedException {
        return MQAdminInstance.threadLocalMQAdminExt().cleanExpiredConsumerQueue(cluster);
    }

    @Override
    public boolean cleanExpiredConsumerQueueByAddr(String addr)
            throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQClientException,
            InterruptedException {
        return MQAdminInstance.threadLocalMQAdminExt().cleanExpiredConsumerQueueByAddr(addr);
    }

    @Override
    public ConsumerRunningInfo getConsumerRunningInfo(String consumerGroup, String clientId, boolean jstack)
            throws RemotingException, MQClientException, InterruptedException {
        return MQAdminInstance.threadLocalMQAdminExt().getConsumerRunningInfo(consumerGroup, clientId, jstack);
    }


    @Override
    public List<MessageTrack> messageTrackDetail(MessageExt msg)
            throws RemotingException, MQClientException, InterruptedException, MQBrokerException {
        return MQAdminInstance.threadLocalMQAdminExt().messageTrackDetail(msg);
    }

    @Override
    public void cloneGroupOffset(String srcGroup, String destGroup, String topic, boolean isOffline)
            throws RemotingException, MQClientException, InterruptedException, MQBrokerException {
        MQAdminInstance.threadLocalMQAdminExt().cloneGroupOffset(srcGroup, destGroup, topic, isOffline);
    }

    @Override
    public void createTopic(String key, String newTopic, int queueNum, Map<String, String> attributes) throws MQClientException {
        MQAdminInstance.threadLocalMQAdminExt().createTopic(key, newTopic, queueNum, attributes);
    }

    @Override
    public void createTopic(String key, String newTopic, int queueNum, int topicSysFlag, Map<String, String> attributes)
            throws MQClientException {
        MQAdminInstance.threadLocalMQAdminExt().createTopic(key, newTopic, queueNum, topicSysFlag, attributes);
    }

    @Override
    public long searchOffset(MessageQueue mq, long timestamp) throws MQClientException {
        return MQAdminInstance.threadLocalMQAdminExt().searchOffset(mq, timestamp);
    }

    @Override
    public long maxOffset(MessageQueue mq) throws MQClientException {
        return MQAdminInstance.threadLocalMQAdminExt().maxOffset(mq);
    }

    @Override
    public long minOffset(MessageQueue mq) throws MQClientException {
        return MQAdminInstance.threadLocalMQAdminExt().minOffset(mq);
    }

    @Override
    public long earliestMsgStoreTime(MessageQueue mq) throws MQClientException {
        return MQAdminInstance.threadLocalMQAdminExt().earliestMsgStoreTime(mq);
    }

    @Override
    public QueryResult queryMessage(String topic, String key, int maxNum, long begin, long end)
            throws MQClientException, InterruptedException {
        return MQAdminInstance.threadLocalMQAdminExt().queryMessage(topic, key, maxNum, begin, end);
    }

    @Override
    @Deprecated
    public void start() throws MQClientException {
        throw new IllegalStateException("thisMethod is deprecated.use org.apache.rocketmq.dashboard.aspect.admin.MQAdminAspect instead of this");
    }

    @Override
    @Deprecated
    public void shutdown() {
        throw new IllegalStateException("thisMethod is deprecated.use org.apache.rocketmq.dashboard.aspect.admin.MQAdminAspect instead of this");
    }

    // below is 3.2.6->3.5.8 updated

    @Override
    public List<QueueTimeSpan> queryConsumeTimeSpan(String topic,
                                                    String group) throws InterruptedException, MQBrokerException, RemotingException, MQClientException {
        return MQAdminInstance.threadLocalMQAdminExt().queryConsumeTimeSpan(topic, group);
    }

    //MessageClientIDSetter.getNearlyTimeFromID has bug,so we subtract half a day
    //next version we will remove it
    //https://issues.apache.org/jira/browse/ROCKETMQ-111
    //https://github.com/apache/incubator-rocketmq/pull/69
    @Override
    public MessageExt viewMessage(String topic,
                                  String msgId) throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        logger.info("MessageClientIDSetter.getNearlyTimeFromID(msgId)={} msgId={}", MessageClientIDSetter.getNearlyTimeFromID(msgId), msgId);
        MQAdminImpl mqAdminImpl = MQAdminInstance.threadLocalMqClientInstance().getMQAdminImpl();
        Set<String> clusterList = MQAdminInstance.threadLocalMQAdminExt().getTopicClusterList(topic);
        if (clusterList == null || clusterList.isEmpty()) {
            QueryResult qr = Reflect.on(mqAdminImpl).call("queryMessage", "", topic, msgId, 32,
                    0L, Long.MAX_VALUE, true).get();
            if (qr != null && qr.getMessageList() != null && !qr.getMessageList().isEmpty()) {
                return qr.getMessageList().get(0);
            }
        } else {
            for (String name : clusterList) {
                QueryResult qr = Reflect.on(mqAdminImpl).call("queryMessage", name, topic, msgId, 32,
                        0L, Long.MAX_VALUE, true).get();
                if (qr != null && qr.getMessageList() != null && !qr.getMessageList().isEmpty()) {
                    return qr.getMessageList().get(0);
                }
            }
        }
        return null;
    }

    @Override
    public ConsumeMessageDirectlyResult consumeMessageDirectly(String consumerGroup, String clientId, String topic,
                                                               String msgId) throws RemotingException, MQClientException, InterruptedException, MQBrokerException {
        return MQAdminInstance.threadLocalMQAdminExt().consumeMessageDirectly(consumerGroup, clientId, topic, msgId);
    }

    @Override
    public ConsumeMessageDirectlyResult consumeMessageDirectly(String s, String s1, String s2, String s3, String s4) throws RemotingException, MQClientException, InterruptedException, MQBrokerException {
        return null;
    }

    @Override
    public Properties getBrokerConfig(
            String brokerAddr) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, UnsupportedEncodingException, InterruptedException, MQBrokerException {
        return MQAdminInstance.threadLocalMQAdminExt().getBrokerConfig(brokerAddr);
    }

    @Override
    public TopicList fetchTopicsByCLuster(
            String clusterName) throws RemotingException, MQClientException, InterruptedException {
        return MQAdminInstance.threadLocalMQAdminExt().fetchTopicsByCLuster(clusterName);
    }

    @Override
    public boolean cleanUnusedTopic(
            String cluster) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQClientException, InterruptedException {
        return MQAdminInstance.threadLocalMQAdminExt().cleanUnusedTopic(cluster);
    }

    @Override
    public boolean cleanUnusedTopicByAddr(
            String addr) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQClientException, InterruptedException {
        return MQAdminInstance.threadLocalMQAdminExt().cleanUnusedTopicByAddr(addr);
    }

    @Override
    public BrokerStatsData viewBrokerStatsData(String brokerAddr, String statsName,
                                               String statsKey) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQClientException, InterruptedException {
        return MQAdminInstance.threadLocalMQAdminExt().viewBrokerStatsData(brokerAddr, statsName, statsKey);
    }

    @Override
    public Set<String> getClusterList(
            String topic) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQClientException, InterruptedException {
        return MQAdminInstance.threadLocalMQAdminExt().getClusterList(topic);
    }

    @Override
    public ConsumeStatsList fetchConsumeStatsInBroker(String brokerAddr, boolean isOrder,
                                                      long timeoutMillis) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQClientException, InterruptedException {
        return MQAdminInstance.threadLocalMQAdminExt().fetchConsumeStatsInBroker(brokerAddr, isOrder, timeoutMillis);
    }

    @Override
    public Set<String> getTopicClusterList(
            String topic) throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        return MQAdminInstance.threadLocalMQAdminExt().getTopicClusterList(topic);
    }

    @Override
    public SubscriptionGroupWrapper getAllSubscriptionGroup(String brokerAddr,
                                                            long timeoutMillis) throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException, MQBrokerException {
        return MQAdminInstance.threadLocalMQAdminExt().getAllSubscriptionGroup(brokerAddr, timeoutMillis);
    }

    @Override
    public SubscriptionGroupWrapper getUserSubscriptionGroup(String brokerAddr,
                                                             long timeoutMillis) throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException, MQBrokerException {
        return MQAdminInstance.threadLocalMQAdminExt().getUserSubscriptionGroup(brokerAddr, timeoutMillis);
    }

    @Override
    public TopicConfigSerializeWrapper getAllTopicConfig(String brokerAddr,
                                                         long timeoutMillis) throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException, MQBrokerException {
        return MQAdminInstance.threadLocalMQAdminExt().getAllTopicConfig(brokerAddr, timeoutMillis);
    }

    @Override
    public TopicConfigSerializeWrapper getUserTopicConfig(String brokerAddr, boolean specialTopic,
                                                          long timeoutMillis) throws InterruptedException, RemotingException, MQBrokerException, MQClientException {
        return MQAdminInstance.threadLocalMQAdminExt().getUserTopicConfig(brokerAddr, specialTopic, timeoutMillis);
    }

    @Override
    public void updateConsumeOffset(String brokerAddr, String consumeGroup, MessageQueue mq,
                                    long offset) throws RemotingException, InterruptedException, MQBrokerException {
        MQAdminInstance.threadLocalMQAdminExt().updateConsumeOffset(brokerAddr, consumeGroup, mq, offset);
    }

    // 4.0.0 added
    @Override
    public void updateNameServerConfig(Properties properties,
                                       List<String> list) throws InterruptedException, RemotingConnectException, UnsupportedEncodingException, RemotingSendRequestException, RemotingTimeoutException, MQClientException, MQBrokerException {

    }

    @Override
    public Map<String, Properties> getNameServerConfig(
            List<String> list) throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException, MQClientException, UnsupportedEncodingException {
        return null;
    }

    @Override
    public QueryConsumeQueueResponseBody queryConsumeQueue(String brokerAddr, String topic,
                                                           int queueId, long index, int count,
                                                           String consumerGroup) throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException, MQClientException {
        return null;
    }

    @Override
    public void exportRocksDBConfigToJson(String s, List<ExportRocksDBConfigToJsonRequestHeader.ConfigType> list) throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException, MQClientException {

    }


    @Override
    public boolean resumeCheckHalfMessage(String topic,
                                          String msgId) throws RemotingException, MQClientException, InterruptedException, MQBrokerException {
        return false;
    }

    @Override
    public void addBrokerToContainer(String brokerContainerAddr, String brokerConfig) throws InterruptedException,
            MQBrokerException, RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addBrokerToContainer'");
    }

    @Override
    public void removeBrokerFromContainer(String brokerContainerAddr, String clusterName, String brokerName,
                                          long brokerId) throws InterruptedException, MQBrokerException, RemotingTimeoutException,
            RemotingSendRequestException, RemotingConnectException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeBrokerFromContainer'");
    }


    @Override
    public TopicStatsTable examineTopicStats(String brokerAddr, String topic)
            throws RemotingException, MQClientException, InterruptedException, MQBrokerException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'examineTopicStats'");
    }

    @Override
    public AdminToolResult<TopicStatsTable> examineTopicStatsConcurrent(String topic) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'examineTopicStatsConcurrent'");
    }

    @Override
    public ConsumeStats examineConsumeStats(String brokerAddr, String consumerGroup, String topicName,
                                            long timeoutMillis) throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException,
            RemotingConnectException, MQBrokerException {
        // TODO Auto-generated method stub
        return MQAdminInstance.threadLocalMQAdminExt().examineConsumeStats(brokerAddr, consumerGroup, topicName, timeoutMillis);
    }

    @Override
    public AdminToolResult<ConsumeStats> examineConsumeStatsConcurrent(String consumerGroup, String topic) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'examineConsumeStatsConcurrent'");
    }

    @Override
    public ConsumerConnection examineConsumerConnectionInfo(String consumerGroup, String brokerAddr)
            throws InterruptedException, MQBrokerException, RemotingException, MQClientException {
        return MQAdminInstance.threadLocalMQAdminExt().examineConsumerConnectionInfo(consumerGroup, brokerAddr);
    }

    @Override
    public ProducerTableInfo getAllProducerInfo(String brokerAddr)
            throws RemotingException, MQClientException, InterruptedException, MQBrokerException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllProducerInfo'");
    }

    @Override
    public void deleteTopic(String topicName, String clusterName)
            throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteTopic'");
    }

    @Override
    public AdminToolResult<BrokerOperatorResult> deleteTopicInBrokerConcurrent(Set<String> addrs, String topic) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteTopicInBrokerConcurrent'");
    }

    @Override
    public void deleteTopicInNameServer(Set<String> addrs, String clusterName, String topic)
            throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteTopicInNameServer'");
    }

    @Override
    public AdminToolResult<BrokerOperatorResult> resetOffsetNewConcurrent(String group, String topic, long timestamp) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resetOffsetNewConcurrent'");
    }

    @Override
    public TopicList queryTopicsByConsumer(String group)
            throws InterruptedException, MQBrokerException, RemotingException, MQClientException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryTopicsByConsumer'");
    }

    @Override
    public AdminToolResult<TopicList> queryTopicsByConsumerConcurrent(String group) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryTopicsByConsumerConcurrent'");
    }

    @Override
    public SubscriptionData querySubscription(String group, String topic)
            throws InterruptedException, MQBrokerException, RemotingException, MQClientException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'querySubscription'");
    }

    @Override
    public AdminToolResult<List<QueueTimeSpan>> queryConsumeTimeSpanConcurrent(String topic, String group) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryConsumeTimeSpanConcurrent'");
    }

    @Override
    public boolean deleteExpiredCommitLog(String cluster) throws RemotingConnectException, RemotingSendRequestException,
            RemotingTimeoutException, MQClientException, InterruptedException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteExpiredCommitLog'");
    }

    @Override
    public boolean deleteExpiredCommitLogByAddr(String addr) throws RemotingConnectException,
            RemotingSendRequestException, RemotingTimeoutException, MQClientException, InterruptedException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteExpiredCommitLogByAddr'");
    }

    @Override
    public ConsumerRunningInfo getConsumerRunningInfo(String consumerGroup, String clientId, boolean jstack,
                                                      boolean metrics) throws RemotingException, MQClientException, InterruptedException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getConsumerRunningInfo'");
    }

    @Override
    public List<MessageTrack> messageTrackDetailConcurrent(MessageExt msg)
            throws RemotingException, MQClientException, InterruptedException, MQBrokerException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'messageTrackDetailConcurrent'");
    }

    @Override
    public void setMessageRequestMode(String brokerAddr, String topic, String consumerGroup, MessageRequestMode mode,
                                      int popWorkGroupSize, long timeoutMillis) throws InterruptedException, RemotingTimeoutException,
            RemotingSendRequestException, RemotingConnectException, MQClientException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setMessageRequestMode'");
    }

    @Override
    public long searchOffset(String brokerAddr, String topicName, int queueId, long timestamp, long timeoutMillis)
            throws RemotingException, MQBrokerException, InterruptedException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchOffset'");
    }

    @Override
    public void resetOffsetByQueueId(String brokerAddr, String consumerGroup, String topicName, int queueId,
                                     long resetOffset) throws RemotingException, InterruptedException, MQBrokerException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resetOffsetByQueueId'");
    }

    @Override
    public void createStaticTopic(String addr, String defaultTopic, TopicConfig topicConfig,
                                  TopicQueueMappingDetail mappingDetail, boolean force)
            throws RemotingException, InterruptedException, MQBrokerException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createStaticTopic'");
    }

    @Override
    public GroupForbidden updateAndGetGroupReadForbidden(String brokerAddr, String groupName, String topicName,
                                                         Boolean readable) throws RemotingException, InterruptedException, MQBrokerException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAndGetGroupReadForbidden'");
    }

    @Override
    public MessageExt queryMessage(String clusterName, String topic, String msgId)
            throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryMessage'");
    }

    @Override
    public HARuntimeInfo getBrokerHAStatus(String brokerAddr) throws RemotingConnectException,
            RemotingSendRequestException, RemotingTimeoutException, InterruptedException, MQBrokerException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBrokerHAStatus'");
    }

    @Override
    public BrokerReplicasInfo getInSyncStateData(String controllerAddress, List<String> brokers)
            throws RemotingException, InterruptedException, MQBrokerException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getInSyncStateData'");
    }

    @Override
    public EpochEntryCache getBrokerEpochCache(String brokerAddr)
            throws RemotingException, InterruptedException, MQBrokerException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBrokerEpochCache'");
    }

    @Override
    public GetMetaDataResponseHeader getControllerMetaData(String controllerAddr)
            throws RemotingException, InterruptedException, MQBrokerException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getControllerMetaData'");
    }

    @Override
    public void resetMasterFlushOffset(String brokerAddr, long masterFlushOffset) throws InterruptedException,
            MQBrokerException, RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resetMasterFlushOffset'");
    }

    @Override
    public Map<String, Properties> getControllerConfig(List<String> controllerServers)
            throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException,
            RemotingConnectException, MQClientException, UnsupportedEncodingException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getControllerConfig'");
    }

    @Override
    public void updateControllerConfig(Properties properties, List<String> controllers)
            throws InterruptedException, RemotingConnectException, UnsupportedEncodingException,
            RemotingSendRequestException, RemotingTimeoutException, MQClientException, MQBrokerException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateControllerConfig'");
    }

    @Override
    public Pair<ElectMasterResponseHeader, BrokerMemberGroup> electMaster(String s, String s1, String s2, Long aLong) throws RemotingException, InterruptedException, MQBrokerException {
        return null;
    }

    @Override
    public void cleanControllerBrokerData(String controllerAddr, String clusterName, String brokerName,
                                          String brokerAddr, boolean isCleanLivingBroker)
            throws RemotingException, InterruptedException, MQBrokerException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cleanControllerBrokerData'");
    }

    @Override
    public void updateColdDataFlowCtrGroupConfig(String brokerAddr, Properties properties)
            throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException,
            UnsupportedEncodingException, InterruptedException, MQBrokerException {
        MQAdminInstance.threadLocalMQAdminExt().updateColdDataFlowCtrGroupConfig(brokerAddr, properties);
    }

    @Override
    public void removeColdDataFlowCtrGroupConfig(String brokerAddr, String consumerGroup)
            throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException,
            UnsupportedEncodingException, InterruptedException, MQBrokerException {
        MQAdminInstance.threadLocalMQAdminExt().removeColdDataFlowCtrGroupConfig(brokerAddr, consumerGroup);
    }

    @Override
    public String getColdDataFlowCtrInfo(String brokerAddr)
            throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException,
            UnsupportedEncodingException, InterruptedException, MQBrokerException {
        return MQAdminInstance.threadLocalMQAdminExt().getColdDataFlowCtrInfo(brokerAddr);
    }

    @Override
    public String setCommitLogReadAheadMode(String brokerAddr, String mode)
            throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException,
            InterruptedException, MQBrokerException, UnsupportedEncodingException {
        return MQAdminInstance.threadLocalMQAdminExt().setCommitLogReadAheadMode(brokerAddr, mode);
    }

    @Override
    public void createUser(String brokerAddr,
                           UserInfo userInfo) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQBrokerException, InterruptedException {
        MQAdminInstance.threadLocalMQAdminExt().createUser(brokerAddr, userInfo);
    }

    @Override
    public void createUser(String brokerAddr, String username, String password,
                           String userType) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQBrokerException, InterruptedException {
        MQAdminInstance.threadLocalMQAdminExt().createUser(brokerAddr, username, password, userType);
    }

    @Override
    public void updateUser(String brokerAddr, String username,
                           String password, String userType,
                           String userStatus) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQBrokerException, InterruptedException {
        MQAdminInstance.threadLocalMQAdminExt().updateUser(brokerAddr, username, password, userType, userStatus);
    }

    @Override
    public void updateUser(String brokerAddr,
                           UserInfo userInfo) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQBrokerException, InterruptedException {
        MQAdminInstance.threadLocalMQAdminExt().updateUser(brokerAddr, userInfo);
    }

    @Override
    public void deleteUser(String brokerAddr,
                           String username) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQBrokerException, InterruptedException {
        MQAdminInstance.threadLocalMQAdminExt().deleteUser(brokerAddr, username);
    }

    @Override
    public UserInfo getUser(String brokerAddr,
                            String username) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQBrokerException, InterruptedException {
        return MQAdminInstance.threadLocalMQAdminExt().getUser(brokerAddr, username);
    }

    @Override
    public List<UserInfo> listUser(String brokerAddr,
                                   String filter) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQBrokerException, InterruptedException {
        return MQAdminInstance.threadLocalMQAdminExt().listUser(brokerAddr, filter);
    }

    @Override
    public void createAcl(String brokerAddr, String subject, List<String> resources, List<String> actions,
                          List<String> sourceIps,
                          String decision) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQBrokerException, InterruptedException {
        MQAdminInstance.threadLocalMQAdminExt().createAcl(brokerAddr, subject, resources, actions, sourceIps, decision);
    }

    @Override
    public void createAcl(String brokerAddr,
                          AclInfo aclInfo) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQBrokerException, InterruptedException {
        MQAdminInstance.threadLocalMQAdminExt().createAcl(brokerAddr, aclInfo);
    }

    @Override
    public void updateAcl(String brokerAddr, String subject, List<String> resources, List<String> actions,
                          List<String> sourceIps,
                          String decision) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQBrokerException, InterruptedException {
        MQAdminInstance.threadLocalMQAdminExt().updateAcl(brokerAddr, subject, resources, actions, sourceIps, decision);
    }

    @Override
    public void updateAcl(String brokerAddr,
                          AclInfo aclInfo) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQBrokerException, InterruptedException {
        MQAdminInstance.threadLocalMQAdminExt().updateAcl(brokerAddr, aclInfo);
    }

    @Override
    public void deleteAcl(String brokerAddr, String subject,
                          String resource) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQBrokerException, InterruptedException {
        MQAdminInstance.threadLocalMQAdminExt().deleteAcl(brokerAddr, subject, resource);
    }

    @Override
    public AclInfo getAcl(String brokerAddr,
                          String subject) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQBrokerException, InterruptedException {
        return MQAdminInstance.threadLocalMQAdminExt().getAcl(brokerAddr, subject);
    }

    @Override
    public List<AclInfo> listAcl(String brokerAddr, String subjectFilter,
                                 String resourceFilter) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, MQBrokerException, InterruptedException {
        return MQAdminInstance.threadLocalMQAdminExt().listAcl(brokerAddr, subjectFilter, resourceFilter);
    }

    @Override
    public void exportPopRecords(String brokerAddr, long timeout) throws RemotingConnectException,
            RemotingSendRequestException, RemotingTimeoutException, MQBrokerException, InterruptedException {
        MQAdminInstance.threadLocalMQAdminExt().exportPopRecords(brokerAddr, timeout);
    }
}
