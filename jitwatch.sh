java -jar target/benchmarks.jar -jvmArgsPrepend "-XX:+UnlockDiagnosticVMOptions -XX:+TraceClassLoading -XX:+LogCompilation -XX:+PrintAssembly -XX:LogFile=jitwatch.log" -p size=256
