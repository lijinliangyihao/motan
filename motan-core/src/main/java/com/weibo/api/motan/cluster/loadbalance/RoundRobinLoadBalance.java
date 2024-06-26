/*
 *  Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.weibo.api.motan.cluster.loadbalance;

import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.rpc.Referer;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.util.MathUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round robin loadbalance.
 *
 * @author fishermen
 * @version V1.0 created at: 2013-6-13
 */
@SpiMeta(name = "roundrobin")
public class RoundRobinLoadBalance<T> extends AbstractLoadBalance<T> {
    private final AtomicInteger idx = new AtomicInteger(0);

    @Override
    protected Referer<T> doSelect(Request request) {
        List<Referer<T>> referers = getReferers();
        Referer<T> ref = referers.get(getNextNonNegative() % referers.size());
        if (ref.isAvailable()) {
            return ref;
        }
        return selectFromRandomStart(referers);
    }

    @Override
    protected void doSelectToHolder(Request request, List<Referer<T>> refersHolder) {
        addToSelectHolderFromStart(getReferers(), refersHolder, getNextNonNegative());
    }

    // get non-negative int
    private int getNextNonNegative() {
        return MathUtil.getNonNegative(idx.incrementAndGet());
    }

    @Override
    public boolean canSelectMulti() {
        return false; //Obtaining multiple referers may cause uneven requests
    }
}
