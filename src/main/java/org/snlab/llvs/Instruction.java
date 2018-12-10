package org.snlab.llvs;

import java.util.HashMap;
import java.util.Map;

public class Instruction {

  public enum Algo {
    ASSIGN("="),
    CEQ("=="),
    CGT(">"),
    CGET(">="),
    CLT("<"),
    CLET("<="),
    PLUS("+"),
    IN("in"),
    UNION("union");

    private String printable;

    Algo(String printable) {
      this.printable = printable;
    }

    @Override
    public String toString() {
      return printable;
    }
  }

  private Variable guard;
  private Variable linput;
  private Variable rinput;
  private Variable output;
  private Algo algo;
  private boolean unary = false;

  private Table table;
  private Table opTable;

  public Variable getGuard() {
    return guard;
  }

  public void setGuard(Variable guard) {
    this.guard = guard;
  }

  public Variable getLinput() {
    return linput;
  }

  public void setLinput(Variable linput) {
    this.linput = linput;
  }

  public Variable getRinput() {
    return rinput;
  }

  public void setRinput(Variable rinput) {
    this.rinput = rinput;
  }

  public Variable getOutput() {
    return output;
  }

  public void setOutput(Variable output) {
    this.output = output;
  }

  public Algo getAlgo() {
    return algo;
  }

  public void setAlgo(Algo algo) {
    this.algo = algo;
  }

  public Table getTable() {
    return table;
  }

  public boolean isUnary() {
    return this.unary;
  }

  public Instruction(Variable guard, Variable linput, Variable output, Algo algo) {
    this.guard = guard;
    this.linput = linput;
    this.output = output;
    this.algo = algo;
    this.unary = true;

    this.output.setInstruction(this);
  }

  public Instruction(Variable guard, Variable linput, Variable rinput, Variable output, Algo algo) {
    this.guard = guard;
    this.linput = linput;
    this.rinput = rinput;
    this.output = output;
    this.algo = algo;

    this.output.setInstruction(this);
  }

  private void generateTableDefaultEntry() {
    this.generateTableDefaultEntry(true);
  }

  private void generateTableDefaultEntry(boolean outputIsGuard) {
    if (outputIsGuard) {
      Map<String, String> guardFalseEntry = new HashMap();
      guardFalseEntry.put("pri", "2");
      guardFalseEntry.put(this.guard.getName(), "*");
      String s = "";
      for (int i = 0; i < this.linput.getBitwidth(); ++i) {
        s += "*";
      }
      guardFalseEntry.put(this.linput.getName(), s);
      if (!this.unary) {
        guardFalseEntry.put(this.rinput.getName(), s);
      }
      guardFalseEntry.put(this.output.getName(), "0");
      this.table.getEntries().add(guardFalseEntry);
    }

    Map<String, String> defaultEntry = new HashMap();
    defaultEntry.put("pri", "1");
    defaultEntry.put(this.guard.getName(), "*");
    String ss = "";
    for (int i = 0; i < this.linput.getBitwidth(); ++i) {
      ss += "*";
    }
    defaultEntry.put(this.linput.getName(), ss);
    if (!this.unary) {
      defaultEntry.put(this.rinput.getName(), ss);
    }
    defaultEntry.put(this.output.getName(), "NOP");
    this.table.getEntries().add(defaultEntry);

  }

  public void generateTable() {
    this.table = new Table();
    table.getSchema().getInputs().add(this.guard.getName());
    table.getSchema().getInputs().add(this.linput.getName());
    if (!this.unary && this.algo != Algo.IN) {
      table.getSchema().getInputs().add(this.rinput.getName());
    }

    table.getSchema().setOutput(this.output.getName());

    if (this.algo == Algo.CLT) {
      for (int i = this.rinput.getBitwidth(); i > 0; --i) {
        int bit = (this.rinput.getValues().get(0) >> (i - 1)) & 1;
        if (bit == 1) {
          Map<String, String> entry = new HashMap<>();
          String s = "";
          for (int j = this.rinput.getBitwidth(); j > 0; --j) {
            if (j > i) {
              int b = (this.rinput.getValues().get(0) >> (j - 1)) & 1;
              s += String.valueOf(b);
            } else if (j == i) {
              s += "0";
            } else {
              s += "*";
            }
          }
          entry.put("pri", "3");
          entry.put(this.guard.getName(), "1");
          entry.put(this.linput.getName(), s);
          entry.put(this.rinput.getName(), String.valueOf(this.rinput.getValues().get(0)));
          entry.put(this.output.getName(), "1");
          this.table.getEntries().add(entry);
        }
      }
      this.generateTableDefaultEntry();
    } else if (this.algo == Algo.CEQ) {
      for (int val : this.rinput.getValues()) {
        Map<String, String> entry = new HashMap<>();
        entry.put("pri", "3");
        entry.put(this.guard.getName(), "1");
        entry.put(this.linput.getName(), "1");
        entry.put(this.rinput.getName(), String.valueOf(val));
        entry.put(this.output.getName(), "1");
        this.table.getEntries().add(entry);
      }
      this.generateTableDefaultEntry();
    } else if (this.algo == Algo.ASSIGN) {

      for (int val : this.linput.getValues()) {
        Map<String, String> entry = new HashMap<>();
        entry.put("pri", "3");
        entry.put(this.guard.getName(), "1");
        entry.put(this.linput.getName(), String.valueOf(val));
        entry.put(this.output.getName(), String.valueOf(val));
        this.table.getEntries().add(entry);
      }
      this.generateTableDefaultEntry(false);
    } else if (this.algo == Algo.PLUS) {
      this.linput.extract();
      this.rinput.extract();
      for (int lval : this.linput.getValues()) {
        for (int rval : this.rinput.getValues()) {
          Map<String, String> entry = new HashMap<>();
          entry.put("pri", "3");
          entry.put(this.guard.getName(), "1");
          entry.put(this.linput.getName(), String.valueOf(lval));
          entry.put(this.rinput.getName(), String.valueOf(rval));
          entry.put(this.output.getName(), String.valueOf(lval + rval));
          this.table.getEntries().add(entry);
        }
      }
      this.generateTableDefaultEntry(false);
    } else if (this.algo == Algo.IN) {
      for (int i : this.rinput.getValues()) {
        Map<String, String> entry = new HashMap<>();
        entry.put("pri", "3");
        entry.put(this.guard.getName(), "1");
        entry.put(this.linput.getName(), String.valueOf(i));
        entry.put(this.output.getName(), "1");
        this.table.getEntries().add(entry);
      }
      this.generateTableDefaultEntry(true);
    }

  }
}
