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

package baiducontest.common.distance;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import baiducontest.common.parameters.DoubleParameter;
import baiducontest.common.parameters.Parameter;
import baiducontest.math.Vector;
import baiducontest.math.Vector.Element;

import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;

/** 
 * Implement Minkowski distance, a real-valued generalization of the 
 * integral L(n) distances: Manhattan = L1, Euclidean = L2. 
 * For high numbers of dimensions, very high exponents give more useful distances. 
 * 
 * Note: Math.pow is clever about integer-valued doubles.
 **/
public class MinkowskiDistanceMeasure implements DistanceMeasure {

  private static final double EXPONENT = 3.0;

  private List<Parameter<?>> parameters;
  private double exponent = EXPONENT;
  
  public MinkowskiDistanceMeasure() {
  }
  
  public MinkowskiDistanceMeasure(double exponent) {
    this.exponent = exponent;
  }

  @Override
  public void createParameters(String prefix, Configuration conf) {
    parameters = Lists.newArrayList();
    Parameter<?> param =
        new DoubleParameter(prefix, "exponent", conf, EXPONENT, "Exponent for Fractional Lagrange distance");
    parameters.add(param);
  }

  @Override
  public Collection<Parameter<?>> getParameters() {
    return parameters;
  }

  @Override
  public void configure(Configuration jobConf) {
    if (parameters == null) {
      ParameteredGeneralizations.configureParameters(this, jobConf);
    }
  }

  public double getExponent() {
    return exponent;
  }

  public void setExponent(double exponent) {
    this.exponent = exponent;
  }

  /**
   *  Math.pow is clever about integer-valued doubles
   */
  @Override
  public double distance(Vector v1, Vector v2) {
    Vector distVector = v1.minus(v2);
    double sum = 0.0;
    Iterator<Element> it = distVector.iterateNonZero();
    while (it.hasNext()) {
      Element e = it.next();
      sum += Math.pow(Math.abs(e.get()), exponent);
    }
    return Math.pow(sum, 1.0 / exponent);
  }

  // TODO: how?
  @Override
  public double distance(double centroidLengthSquare, Vector centroid, Vector v) {
    return distance(centroid, v); // TODO - can this use centroidLengthSquare somehow?
  }

}
