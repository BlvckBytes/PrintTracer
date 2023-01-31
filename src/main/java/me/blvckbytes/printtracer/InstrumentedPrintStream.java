package me.blvckbytes.printtracer;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class InstrumentedPrintStream extends PrintStream {

  private final Consumer<List<StackWalker.StackFrame>> onPrintLn;

  public InstrumentedPrintStream(OutputStream out, Consumer<List<StackWalker.StackFrame>> onPrintLn) {
    super(out);
    this.onPrintLn = onPrintLn;
  }

  @Override
  public void println(String x) {
    StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    List<StackWalker.StackFrame> frames = walker.walk(stream -> stream.collect(Collectors.toList()));
    this.onPrintLn.accept(frames);
    super.println(x);
  }
}
