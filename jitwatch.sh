# java -jar target/benchmarks.jar -jvmArgsPrepend "-XX:+UnlockDiagnosticVMOptions -XX:+TraceClassLoading -XX:+LogCompilation -XX:+PrintAssembly -XX:LogFile=jitwatch.log" -p size=65536
java -XX:+UnlockDiagnosticVMOptions -XX:+TraceClassLoading -XX:+LogCompilation -XX:+PrintAssembly -XX:LogFile=jitwatch.log -cp target/benchmarks.jar com.julienviet.benchmarks.Main

