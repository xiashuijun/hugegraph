// Copyright 2017 hugegraph Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.baidu.hugegraph.blueprints.process;

import com.baidu.hugegraph.HBaseStorageSetup;
import com.baidu.hugegraph.blueprints.HBaseGraphProvider;
import com.baidu.hugegraph.core.HugeGraph;
import org.apache.hadoop.hbase.util.VersionInfo;
import org.apache.tinkerpop.gremlin.GraphProviderClass;
import org.apache.tinkerpop.gremlin.process.ProcessStandardSuite;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.io.IOException;

/**
 */
@RunWith(ProcessStandardSuite.class)
@GraphProviderClass(provider = HBaseGraphProvider.class, graph = HugeGraph.class)
public class HBaseProcessTest {

    @BeforeClass
    public static void startHBase() {
        try {
            HBaseStorageSetup.startHBase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void stopHBase() {
        // Workaround for https://issues.apache.org/jira/browse/HBASE-10312
        if (VersionInfo.getVersion().startsWith("0.96"))
            HBaseStorageSetup.killIfRunning();
    }
}
