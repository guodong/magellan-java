package org.snlab.llvs;

import static org.fusesource.jansi.Ansi.ansi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.fusesource.jansi.Ansi.Color;

public class Table {



  private Schema schema = new Schema();

  private List<Map> entries = new ArrayList<>();

  public Schema getSchema() {
    return schema;
  }

  public void setSchema(Schema schema) {
    this.schema = schema;
  }

  public List<Map> getEntries() {
    return entries;
  }

  public void setEntries(List<Map> entries) {
    this.entries = entries;
  }

  public void print() {
    this.schema.print();
    for (Map<String, String> entry : this.entries) {

      System.out.print(entry.get("pri") + " " + entry.get(this.schema.getOutput()) + " ");
      for (String input : this.schema.getInputs()) {
        System.out.print(entry.get(input) + " ");
      }
      System.out.print('\n');
    }
  }

  public void join(Table table) {

  }

}
