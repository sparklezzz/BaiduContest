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

package baiducontest.vectorizer;

import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.Similarity;

public class TFIDF implements Weight {
  
  private Similarity sim = new DefaultSimilarity();
  
  public TFIDF() { }
  
  public TFIDF(Similarity sim) {
    this.sim = sim;
  }
  
  @Override
  public double calculate(int tf, int df, int length, int numDocs) {
    // ignore length
    return sim.tf(tf) * sim.idf(df, numDocs);
  }
}
