package org.snlab.llvs;

import static org.fusesource.jansi.Ansi.ansi;

import java.util.ArrayList;
import java.util.List;
import org.fusesource.jansi.Ansi.Color;

public class Schema {
  private List<String> meta = new ArrayList<>();
  private List<String> inputs = new ArrayList<>();
  private String output;

  public List<String> getMeta() {
    return meta;
  }

  public void setMeta(List<String> meta) {
    this.meta = meta;
  }

  public List<String> getInputs() {
    return inputs;
  }

  public void setInputs(List<String> inputs) {
    this.inputs = inputs;
  }

  public String getOutput() {
    return output;
  }

  public void setOutput(String output) {
    this.output = output;
  }

  public void print() {
    System.out.print("pri | ");
    System.out.print(ansi().fg(Color.GREEN).a(this.output + " | ").reset());
    for (String input : this.inputs) {
      System.out.print(ansi().eraseScreen().fg(Color.RED).a(input + " | ").reset());
    }
    System.out.print('\n');
  }
}
