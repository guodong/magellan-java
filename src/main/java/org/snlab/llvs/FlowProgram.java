package org.snlab.llvs;

import static org.fusesource.jansi.Ansi.ansi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.fusesource.jansi.Ansi.*;

import org.snlab.llvs.Instruction.Algo;

public class FlowProgram {

  private List<Variable> variables = new ArrayList<>();
  private List<Instruction> instructions = new ArrayList<>();

  public void addVariable(Variable variable) {
    this.variables.add(variable);
  }

  public void addInstruction(Instruction instruction) {
    this.instructions.add(instruction);
  }

  public Variable newVariable() {
    Variable v =  new Variable();
    variables.add(v);

    return v;
  }

  public Variable newVariable(String name) {
    Variable v = new Variable(name);
    variables.add(v);

    return v;
  }

  public Variable newVariable(int value) {
    Variable v = new Variable(value);
    variables.add(v);

    return v;
  }

  public Variable getVariable(String name) {
    for (Variable v : this.variables) {
      if (v.getName().equals(name)) {
        return v;
      }
    }

    return null;
  }

  public Instruction newUnaryInstruction(Variable guard, Variable linput, Variable output,
      Algo algo) {
    Instruction inst = new Instruction(guard, linput, output, algo);
    this.instructions.add(inst);
    return inst;
  }

  public Instruction newInstruction(Variable guard, Variable linput, Variable rinput,
      Variable output, Algo algo) {
    Instruction inst = new Instruction(guard, linput, rinput, output, algo);
    this.instructions.add(inst);
    return inst;
  }

  public void print() {
    for (Instruction inst : this.instructions) {
      if (inst.isUnary()) {
        System.out.println(
            inst.getGuard().getName() + ": " + inst.getOutput().getName() + " = " + inst.getLinput().getName());
      } else {
        System.out.println(
            inst.getGuard().getName() + ": " + inst.getOutput().getName() + " = " + inst.getLinput().getName() + " " + inst.getAlgo()
                .toString() + " " + inst.getRinput().getName());
      }
    }
  }

  public void generatePipeline() {
    for (Instruction inst : this.instructions) {
      inst.generateTable();
    }
  }

  public void printPipeline() {
    System.out.println("+++++++++++ Print PIT Pipeline +++++++++++");
    for (Instruction inst : this.instructions) {
      System.out.print("pri | ");
      System.out.print(ansi().fg(Color.GREEN).a(inst.getTable().output + " | ").reset());
      for (String input : inst.getTable().inputs) {
        System.out.print(ansi().eraseScreen().fg(Color.RED).a(input + " | ").reset());
      }
      System.out.print('\n');
//      System.out.println(inst.getTable().entries.size());

      // print entries
      for (Map<String, String> entry : inst.getTable().entries) {

        System.out.print(entry.get("pri") + " " + entry.get(inst.getTable().output) + " ");
        for (String input : inst.getTable().inputs) {
          System.out.print(entry.get(input) + " ");
        }
        System.out.print('\n');
      }
      System.out.print('\n');
    }
  }

  public void flowExplore() {

  }

}
