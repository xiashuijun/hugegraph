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

package com.baidu.hugegraph.hadoop.formats;

import org.apache.hadoop.mapreduce.Job;

import java.io.IOException;

/**
 * If an Input- or OutputFormat requires a dynamic configuration of the job at execution time, then a JobConfigurationFormat can be implemented.
 *
 */
public interface JobConfigurationFormat {

    public void updateJob(Job job) throws InterruptedException, IOException;
}
