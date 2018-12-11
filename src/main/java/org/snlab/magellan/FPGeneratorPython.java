package org.snlab.magellan;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.snlab.llvs.ConstantVariable;
import org.snlab.llvs.FlowProgram;
import org.snlab.llvs.Instruction;
import org.snlab.llvs.Instruction.Algo;
import org.snlab.llvs.Variable;
import org.snlab.llvs.Variable.Type;
import org.snlab.magellan.parser.Java9BaseVisitor;
import org.snlab.magellan.parser.Java9Parser;
import org.snlab.magellan.parser.Python3BaseVisitor;
import org.snlab.magellan.parser.Python3Parser;
import org.snlab.magellan.parser.Python3Visitor;

public class FPGeneratorPython extends Python3BaseVisitor<Variable> {

  private FlowProgram program;
  private Stack<Variable> guardStack = new Stack<>();

  public FlowProgram getProgram() {
    return program;
  }

  public FPGeneratorPython() {
    this.program = new FlowProgram();
    Variable guard = program.newVariable();
    this.guardStack.push(guard);
  }

  @Override
  public Variable visitIf_stmt(Python3Parser.If_stmtContext ctx) {
    Variable linput = visit(ctx.test(0));
    Variable rinput = program.newVariable(1);
    Variable output = program.newVariable();

    program.newInstruction(this.guardStack.peek(), linput, rinput, output, Algo.CEQ);

    this.guardStack.push(output);
    visit(ctx.suite(0));
    this.guardStack.pop();

    return null;
  }

  @Override
  public Variable visitAtom(Python3Parser.AtomContext ctx) {
    Variable v = program.newVariable(ctx.getText());
    if (this.isNumeric(ctx.getText())) {
      v.addValue(Integer.parseInt(ctx.getText()));
    }

    return v;
  }

  @Override
  public Variable visitAtom_expr(Python3Parser.Atom_exprContext ctx) {
    String name = ctx.atom().getText();
    for (Python3Parser.TrailerContext t : ctx.trailer()) {
      name += t.getText();
    }
    Variable v = program.newVariable(name);

    if (this.isNumeric(ctx.atom().getText())) {
      v.addValue(Integer.parseInt(ctx.getText()));
    }

    return v;
  }

  @Override
  public Variable visitComparison(Python3Parser.ComparisonContext ctx) {
    if (ctx.expr().size() > 1) {
      Variable linput = visit(ctx.expr(0));
      Variable rinput = visit(ctx.expr(1));
      Variable output = program.newVariable();

      program.newInstruction(this.guardStack.peek(), linput, rinput, output, Algo.CLT);

      return output;
    } else {
      return visit(ctx.expr(0));
    }
  }

  @Override
  public Variable visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {
    Variable linput = visit(ctx.testlist_star_expr(1));
    Variable output = visit(ctx.testlist_star_expr(0));

    program.newUnaryInstruction(this.guardStack.peek(), linput, output, Algo.ASSIGN);

    return null;
  }

  @Override
  public Variable visitArith_expr(Python3Parser.Arith_exprContext ctx) {
    if (ctx.term().size() > 1) {
      Variable output = program.newVariable();
      Variable linput = visit(ctx.term(0));
      Variable rinput = visit(ctx.term(1));

      program.newInstruction(this.guardStack.peek(), linput, rinput, output, Algo.PLUS);

      return output;
    } else {
      return visit(ctx.term(0));
    }
  }

  @Override
  public Variable visitTerm(Python3Parser.TermContext ctx) {
    if (ctx.factor().size() > 1) {
      Variable output = program.newVariable();
      Variable linput = visit(ctx.factor(0));
      Variable rinput = visit(ctx.factor(1));

      program.newInstruction(this.guardStack.peek(), linput, rinput, output, Algo.PLUS);

      return output;
    } else {
      return visit(ctx.factor(0));
    }
  }

  private boolean isNumeric(String str) {
    Pattern pattern = Pattern.compile("[0-9]*");
    Matcher isNum = pattern.matcher(str);
    if (!isNum.matches()) {
      return false;
    }
    return true;
  }

}
