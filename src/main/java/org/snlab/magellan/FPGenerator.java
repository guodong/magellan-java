package org.snlab.magellan;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.snlab.llvs.FlowProgram;
import org.snlab.llvs.Instruction;
import org.snlab.llvs.Instruction.Algo;
import org.snlab.llvs.Variable;
import org.snlab.llvs.Variable.Type;
import org.snlab.magellan.parser.Java9BaseVisitor;
import org.snlab.magellan.parser.Java9Parser;

public class FPGenerator extends Java9BaseVisitor<Variable> {

  private FlowProgram program;
  private Stack<Variable> guardStack = new Stack<>();

  public FlowProgram getProgram() {
    return program;
  }

  public FPGenerator() {
    this.program = new FlowProgram();
    Variable guard = program.newVariable();
    this.guardStack.push(guard);
  }

  @Override
  public Variable visitImportDeclaration(Java9Parser.ImportDeclarationContext ctx) {
    return null;
  }

  @Override
  public Variable visitNormalClassDeclaration(Java9Parser.NormalClassDeclarationContext ctx) {
    return visit(ctx.classBody());
  }

  @Override
  public Variable visitIfThenStatement(Java9Parser.IfThenStatementContext ctx) {
    Variable linput = visit(ctx.expression());
    Variable rinput = program.newVariable(1);
    Variable output = program.newVariable();

    Instruction instruction = program
        .newInstruction(this.guardStack.peek(), linput, rinput, output, Algo.CEQ);

    this.guardStack.push(output);
    visit(ctx.statement());
    this.guardStack.pop();

    return null;
  }

  @Override
  public Variable visitRelationalExpression(Java9Parser.RelationalExpressionContext ctx) {
    if (ctx.relationalExpression() != null) {
      Variable l = visit(ctx.relationalExpression());
      Variable r = visit(ctx.shiftExpression());
      Variable o = program.newVariable();
      program.newInstruction(this.guardStack.peek(), l, r, o, Algo.CLT);

      return o;
    } else {
      return visit(ctx.shiftExpression());
    }
  }

  @Override
  public Variable visitExpressionName(Java9Parser.ExpressionNameContext ctx) {
    return program.newVariable(ctx.identifier().getText());
  }

  @Override
  public Variable visitLiteral(Java9Parser.LiteralContext ctx) {
    Variable v = program.newVariable(Integer.parseInt(ctx.IntegerLiteral().getText()));

    return v;
  }

  @Override
  public Variable visitAssignment(Java9Parser.AssignmentContext ctx) {
    Variable output = visit(ctx.leftHandSide());
    Variable linput = visit(ctx.expression());

    Instruction instruction = program
        .newUnaryInstruction(this.guardStack.peek(), linput, output, Algo.ASSIGN);

    return null;
  }

  @Override
  public Variable visitAdditiveExpression(Java9Parser.AdditiveExpressionContext ctx) {
    if (ctx.additiveExpression() != null) {
      Variable linput = visit(ctx.additiveExpression());
      Variable rinput = visit(ctx.multiplicativeExpression());
      Variable output = program.newVariable();

      Instruction instruction = program
          .newInstruction(this.guardStack.peek(), linput, rinput, output, Algo.PLUS);

      return output;
    } else {
      return visit(ctx.multiplicativeExpression());
    }
  }

  @Override
  public Variable visitVariableDeclarator(Java9Parser.VariableDeclaratorContext ctx) {
    Variable linput = visit(ctx.variableInitializer());
    Variable output = visit(ctx.variableDeclaratorId());

    // If the linput is array, just assign values to output
    if (linput.getType() == Type.ARRAY) {
      for (int v : linput.getValues()) {
        output.addValue(v);
      }
    } else {
      program.newUnaryInstruction(this.guardStack.peek(), linput, output, Algo.ASSIGN);
    }
    return output;
  }

  @Override
  public Variable visitVariableDeclaratorId(Java9Parser.VariableDeclaratorIdContext ctx) {
    return program.newVariable(ctx.identifier().getText());
  }

  @Override
  public Variable visitTypeName(Java9Parser.TypeNameContext ctx) {
    Variable v = program.getVariable(ctx.getText());
    if (v == null) {
      System.out.println("Cannot find variable: " + ctx.getText());
      System.exit(1);
    }

    return v;
  }

  /**
   * Current used for supporting array
   */
  @Override
  public Variable visitMethodInvocation_lfno_primary(
      Java9Parser.MethodInvocation_lfno_primaryContext ctx) {
    if (ctx.identifier().getText().equals("contains")) {
      Variable linput = visit(ctx.argumentList().expression(0));
      Variable rinput = visit(ctx.typeName());
      Variable output = program.newVariable();

      program.newInstruction(this.guardStack.peek(), linput, rinput, output, Algo.IN);

      return output;
    }

    Variable output = program.newVariable();
    for (Java9Parser.ExpressionContext expr : ctx.argumentList().expression()) {
      Variable tmp = visit(expr);
      output.addValue(tmp.getValues().get(0));
    }
    output.setType(Type.ARRAY);
    return output;
  }

  @Override
  public Variable visitArgumentList(Java9Parser.ArgumentListContext ctx) {
    Variable linput = visit(ctx.expression(0));
    Variable rinput = visit(ctx.expression(1));
    Variable output = program.newVariable();

    program.newInstruction(this.guardStack.peek(), linput, rinput, output, Algo.UNION);

    return output;
  }
}
