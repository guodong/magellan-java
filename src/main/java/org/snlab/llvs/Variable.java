package org.snlab.llvs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Variable {

  public enum Type {
    DEFAULT,
    ARRAY,
    MAP
  }

  private Type type = Type.DEFAULT;

  /**
   * A counter for generate variable id and name. eg. %1, %2...
   */
  private static final AtomicInteger counter = new AtomicInteger(0);
  private int id;
  private String name;
  private int bitwidth;

  private List<Integer> values = new ArrayList<>();

  private Instruction instruction;

  public Variable() {
    this.setId(counter.getAndIncrement());
    this.setName("%" + this.id);
    this.setBitwidth(8);
  }

  /**
   * Constant integer variable. eg. a = 5, the 5 is this variable
   * @param value
   */
  public Variable(int value) {
    this();
    this.getValues().add(value);
  }

  public Variable(String name) {
    this(name, 8);
  }

  public Variable(String name, int bitwidth) {
    this.id = counter.incrementAndGet();
    this.setName(name);
    this.setBitwidth(bitwidth);
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getBitwidth() {
    return bitwidth;
  }

  public void setBitwidth(int bitwidth) {
    this.bitwidth = bitwidth;
  }

  public List<Integer> getValues() {
    return values;
  }

  public void setValues(List<Integer> values) {
    this.values = values;
  }

  public Instruction getInstruction() {
    return instruction;
  }

  public void setInstruction(Instruction instruction) {
    this.instruction = instruction;
  }

  public void addValue(int value) {
    if (this.values.contains(value)) {
      return;
    }

    this.values.add(value);
  }

  public void extract() {
    // if already extracted, do not do it again
    if (this.values.size() > 0) {
      return;
    }

    for (int i = 0; i < (1 << this.bitwidth); ++i) {
      this.values.add(i);
    }
  }
}
