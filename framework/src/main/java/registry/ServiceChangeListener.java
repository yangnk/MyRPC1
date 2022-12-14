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

package registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.log4j.Logger;
import utils.ReferenceUtil;

public class ServiceChangeListener implements CuratorListener {

    private Logger logger = Logger.getLogger(ServiceChangeListener.class);

    private String refrenceName;

    public ServiceChangeListener(String refrenceName) {
        this.refrenceName = refrenceName;
    }
    @Override
    public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
        logger.info("监听到服务变化，refrenceName=" + refrenceName);
        ReferenceUtil.get(refrenceName).getReferences();
    }
}
