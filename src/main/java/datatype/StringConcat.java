package datatype;

import uk.ac.manchester.tornado.api.ImmutableTaskGraph;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;

import java.util.Arrays;

public class StringConcat {
    public static void add(String[] a, String[] b, String[] c) {
        for (@Parallel int i = 0; i < c.length; i++) {
            c[i] = a[i].concat(b[i]);
        }
    }

    public static void main(String[] args) {

        final int numElements = 8;
        String[] a = new String[numElements];
        String[] b = new String[numElements];
        String[] c = new String[numElements];

        Arrays.fill(a, "1");
        Arrays.fill(b, "2");
        Arrays.fill(c, "3");

        TaskGraph taskGraph = new TaskGraph("s0") //
                .transferToDevice(DataTransferMode.FIRST_EXECUTION, a, b) //
                .task("t0", StringConcat::add, a, b, c) //
                .transferToHost(DataTransferMode.EVERY_EXECUTION, c);

        ImmutableTaskGraph immutableTaskGraph = taskGraph.snapshot();
        TornadoExecutionPlan executor = new TornadoExecutionPlan(immutableTaskGraph);
        executor.execute();

        System.out.println("a: " + Arrays.toString(a));
        System.out.println("b: " + Arrays.toString(b));
        System.out.println("c: " + Arrays.toString(c));
    }
}
