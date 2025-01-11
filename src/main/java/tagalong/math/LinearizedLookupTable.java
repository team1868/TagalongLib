/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.math;

import java.util.Arrays;
import java.util.Comparator;

public class LinearizedLookupTable {
  private double[] _ids;
  private double[] _values;
  private double max, min;

  /**
   * Constructs a lookup table containing ids and their corresponding values
   */
  public LinearizedLookupTable(double[] id, double[] values) {
    if (id.length != values.length)
      throw new IllegalArgumentException();
    int length = id.length;

    // 1. Fill third array with indices
    Integer[] c = new Integer[length];
    for (int i = 0; i < length; ++i) c[i] = i;

    // 2. Sort third array, with the comparator peaking into original arrays
    // TODO: put your own comparator function here
    Arrays.sort(c, Comparator.comparing(i -> { return id[i]; }));

    // 3. Populate new arrays using the indices
    _ids = new double[length];
    _values = new double[length];
    for (int i = 0; i < length; i++) {
      int newI = c[i];
      _ids[i] = id[newI];
      _values[i] = values[newI];
    }

    max = _ids[_ids.length - 1];
    min = _ids[0];
  }

  /**
   * Returns the value that the id maps to on a continuous, linearized function
   *
   * @param id id contained in the lookup table
   * @return value using linearized lookup
   */
  public double lookup(double id) {
    double newId = Math.max(Math.min(id, max), min);

    int index = Arrays.binarySearch(_ids, newId);
    if (index < 0) {
      int lIndex = -index - 1;
      int hIndex = -index;
      if (hIndex >= _ids.length) {
        hIndex = _ids.length - 1;
        lIndex = _ids.length - 2;
      }
      double slope = (_values[hIndex] - _values[lIndex]) / (_ids[hIndex] - _ids[lIndex]);
      return _values[lIndex] + slope * (newId - _ids[lIndex]);
    } else {
      return _values[index];
    }
  }

  public int size() {
    return _ids.length;
  }
}
