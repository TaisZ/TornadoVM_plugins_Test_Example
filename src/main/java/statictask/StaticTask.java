package statictask;

import uk.ac.manchester.tornado.api.ImmutableTaskGraph;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;

import java.util.Arrays;

public class StaticTask {
    public static void add(int[] a, int[] b, int[] c) {
        for (@Parallel int i = 0; i < c.length; i++) {
            c[i] = a[i] + b[i];
        }
    }

    static final int numElements = 8;
    static int[] a = new int[numElements];
    static int[] b = new int[numElements];
    static int[] c = new int[numElements];

    static {
        Arrays.fill(a, 1);
        Arrays.fill(b, 2);
        Arrays.fill(c, 0);
    }
    static TaskGraph taskGraph = new TaskGraph("s0");
    static {
        taskGraph.transferToDevice(DataTransferMode.FIRST_EXECUTION, a, b) //
                .task("t0",StaticTaskgraph::add, a, b, c) //
                .transferToHost(DataTransferMode.EVERY_EXECUTION, c);
        ImmutableTaskGraph immutableTaskGraph = taskGraph.snapshot();
        TornadoExecutionPlan executor = new TornadoExecutionPlan(immutableTaskGraph);
        executor.execute();
    }

    public static void main(String[] args) {
        System.out.println("a: " + Arrays.toString(a));
        System.out.println("b: " + Arrays.toString(b));
        System.out.println("c: " + Arrays.toString(c));
    }
}
