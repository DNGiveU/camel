/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.spi;

import org.apache.camel.CamelContextAware;
import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.NonManagedService;
import org.apache.camel.Service;

/**
 * Factory used by {@link Consumer} to create Camel {@link Exchange} holding the incoming message received by the
 * consumer.
 * <p/>
 * This factory is only for {@link Consumer}'s to give control on how {@link Exchange} are created and comes into Camel.
 * Each Camel component that provides a {@link Consumer} should use this {@link ExchangeFactory}. There may be other
 * parts in Camel that creates {@link Exchange} such as sub exchanges from Splitter EIP, but they are not part of this
 * contract as we only want to control the created {@link Exchange} that comes into Camel via {@link Consumer} or
 * {@link org.apache.camel.PollingConsumer}.
 * <p/>
 * The factory is pluggable which allows to use different strategies. The default factory will create a new
 * {@link Exchange} instance, and the pooled factory will pool and reuse exchanges.
 */
public interface ExchangeFactory extends Service, CamelContextAware, NonManagedService, RouteIdAware {

    /**
     * Utilization statistics of the this factory.
     */
    interface Statistics {

        /**
         * Number of new exchanges created.
         */
        long getCreatedCounter();

        /**
         * Number of exchanges acquired (reused) when using pooled factory.
         */
        long getAcquiredCounter();

        /**
         * Number of exchanges released back to pool
         */
        long getReleasedCounter();

        /**
         * Number of exchanges discarded (thrown away) such as if no space in cache pool.
         */
        long getDiscardedCounter();

        /**
         * Reset the counters
         */
        void reset();

        /**
         * Whether statistics is enabled.
         */
        boolean isStatisticsEnabled();

        /**
         * Sets whether statistics is enabled.
         *
         * @param statisticsEnabled <tt>true</tt> to enable
         */
        void setStatisticsEnabled(boolean statisticsEnabled);
    }

    /**
     * Service factory key.
     */
    String FACTORY = "exchange-factory";

    /**
     * The consumer using this factory.
     */
    Consumer getConsumer();

    /**
     * Creates a new {@link ExchangeFactory} that is private for the given consumer.
     *
     * @param  consumer the consumer that will use the created {@link ExchangeFactory}
     * @return          the created factory.
     */
    ExchangeFactory newExchangeFactory(Consumer consumer);

    /**
     * Gets a new {@link Exchange}
     *
     * @param autoRelease whether to auto release the exchange when routing is complete via {@link UnitOfWork}
     */
    Exchange create(boolean autoRelease);

    /**
     * Gets a new {@link Exchange}
     *
     * @param autoRelease  whether to auto release the exchange when routing is complete via {@link UnitOfWork}
     * @param fromEndpoint the from endpoint
     */
    Exchange create(Endpoint fromEndpoint, boolean autoRelease);

    /**
     * Releases the exchange back into the pool
     *
     * @param  exchange the exchange
     * @return          true if released into the pool, or false if something went wrong and the exchange was discarded
     */
    default boolean release(Exchange exchange) {
        return true;
    }

    /**
     * The capacity the pool (for each consumer) uses for storing exchanges. The default capacity is 100.
     */
    int getCapacity();

    /**
     * The current number of exchanges in the pool
     */
    int getSize();

    /**
     * The capacity the pool (for each consumer) uses for storing exchanges. The default capacity is 100.
     */
    void setCapacity(int capacity);

    /**
     * Whether statistics is enabled.
     */
    boolean isStatisticsEnabled();

    /**
     * Whether statistics is enabled.
     */
    void setStatisticsEnabled(boolean statisticsEnabled);

    /**
     * Reset the statistics
     */
    void resetStatistics();

    /**
     * Purges the internal cache (if pooled)
     */
    void purge();

    /**
     * Gets the usage statistics
     */
    Statistics getStatistics();

}
