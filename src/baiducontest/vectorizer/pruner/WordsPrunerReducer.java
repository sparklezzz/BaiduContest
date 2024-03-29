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

import com.google.common.base.Preconditions;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Reducer;
import baiducontest.common.Pair;
import baiducontest.common.iterator.sequencefile.SequenceFileIterable;
import baiducontest.math.Vector;
import baiducontest.math.VectorWritable;
import baiducontest.vectorizer.HighDFWordsPruner;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;

public class WordsPrunerReducer extends
        Reducer<WritableComparable<?>, VectorWritable, WritableComparable<?>, VectorWritable> {

  private final HashMap<Integer, Long> dictionary = new HashMap<Integer, Long>();
  private long maxDf = -1;

  @Override
  protected void reduce(WritableComparable<?> key, Iterable<VectorWritable> values, Context context)
          throws IOException, InterruptedException {
    Iterator<VectorWritable> it = values.iterator();
    if (!it.hasNext()) {
      return;
    }
    Vector value = it.next().get();
    Vector vector = value.clone();
    if (maxDf > -1) {
      Iterator<Vector.Element> it1 = value.iterateNonZero();
      while (it1.hasNext()) {
        Vector.Element e = it1.next();
        if (!dictionary.containsKey(e.index())) {
          vector.setQuick(e.index(), 0.0);
          continue;
        }
        long df = dictionary.get(e.index());
        if (df > maxDf) {
          vector.setQuick(e.index(), 0.0);
        }
      }
    }

    VectorWritable vectorWritable = new VectorWritable(vector);
    context.write(key, vectorWritable);
  }

  @Override
  protected void setup(Context context) throws IOException, InterruptedException {
    super.setup(context);
    Configuration conf = context.getConfiguration();
    URI[] localFiles = DistributedCache.getCacheFiles(conf);
    Preconditions.checkArgument(localFiles != null && localFiles.length >= 1,
            "missing paths from the DistributedCache");

    maxDf = conf.getLong(HighDFWordsPruner.MAX_DF, -1);

    Path dictionaryFile = new Path(localFiles[0].getPath());
    // key is feature, value is the document frequency
    for (Pair<IntWritable, LongWritable> record :
            new SequenceFileIterable<IntWritable, LongWritable>(dictionaryFile, true, conf)) {
      dictionary.put(record.getFirst().get(), record.getSecond().get());
    }
  }
}
