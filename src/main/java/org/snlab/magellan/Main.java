package org.snlab.magellan;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.snlab.magellan.parser.*;

public class Main {

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Please specify input program");
      return;
    }

    try {
      String inputFile = Main.class.getClassLoader().getResource(args[0]).getPath();
      System.out.println("Read program from: " + inputFile);
      CharStream input = CharStreams.fromFileName(inputFile);
//      Java9Lexer lexer = new Java9Lexer(input);
//      Java9Parser parser = new Java9Parser(new CommonTokenStream(lexer));
//      ParseTree tree = parser.compilationUnit();
      Python3Lexer lexer = new Python3Lexer(input);
      Python3Parser parser = new Python3Parser(new CommonTokenStream(lexer));
      ParseTree tree = parser.file_input();
      FPGeneratorPython generator = new FPGeneratorPython();
      generator.visit(tree);
      generator.getProgram().print();
      generator.getProgram().generatePipeline();
//      generator.getProgram().printPipeline();

    } catch (IOException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
