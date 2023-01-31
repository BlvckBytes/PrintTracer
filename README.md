# PrintTracer

This little plugin allows you to find out who's calling `System.out.println`, which - in essence - includes
all logging facilities that print to the server console. The system's `out` print-stream is wrapped, and it's
`println` method is instrumented. When calls occur, the current stacktrace is captured and walked, where the
containing package of each invocation's containing class is checked against all packages of known plugins. This
plugin is only meant as a means to debug verbose consoles and should not be used in production.