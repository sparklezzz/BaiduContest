package baiducontest.vectorizer.pruner;
/**
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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Reducer;
import baiducontest.math.Vector;
import baiducontest.math.VectorWritable;
import baiducontest.math.function.Functions;
import baiducontest.vectorizer.common.PartialVectorMerger;

import java.io.IOException;

public class PrunedPartialVectorMergeReducer extends
        Reducer<WritableComparable<?>, VectorWritable, WritableComparable<?>, VectorWritable> {

  private double normPower;

  private boolean logNormalize;

  @Override
  protected void reduce(WritableComparable<?> key, Iterable<VectorWritable> values, Context context) throws IOException,
          InterruptedException {

    Vector vector = null;
    for (VectorWritable value : values) {
      if (vector == null) {
        vector = value.get().clone();
        continue;
      }
      //value.get().addTo(vector);
      vector.assign(value.get(), Functions.PLUS);
    }

    if (normPower != PartialVectorMerger.NO_NORMALIZING) {
      if (logNormalize) {
        vector = vector.logNormalize(normPower);
      } else {
        vector = vector.normalize(normPower);
      }
    }

    VectorWritable vectorWritable = new VectorWritable(vector);
    context.write(key, vectorWritable);
  }

  @Override
  protected void setup(Context context) throws IOException, InterruptedException {
    super.setup(context);
    Configuration conf = context.getConfiguration();
    normPower = conf.getFloat(PartialVectorMerger.NORMALIZATION_POWER, PartialVectorMerger.NO_NORMALIZING);
    logNormalize = conf.getBoolean(PartialVectorMerger.LOG_NORMALIZE, false);
  }
}
