package org.snlab.llvs.table;

import java.util.ArrayList;
import java.util.List;

public class EntryValue {

  private List<Character> bits = new ArrayList<>();

  public List<Character> getBits() {
    return bits;
  }

  public void setBits(List<Character> bits) {
    this.bits = bits;
  }

  public String toString() {
    return bits.toString();
  }
}
