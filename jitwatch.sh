java -XX:+UnlockDiagnosticVMOptions -XX:+TraceClassLoading -XX:+LogCompilation -XX:+PrintAssembly -XX:LogFile=jitwatch.log -jar target/benchmarks.jar -p size=256 offHeap
