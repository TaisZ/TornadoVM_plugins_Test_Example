package uk.ac.manchester.tornado.examples.exception;

import uk.ac.manchester.tornado.api.ImmutableTaskGraph;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;

import javax.print.attribute.standard.RequestingUserName;
import java.util.Arrays;

/**
 * The for loop will throw a ArraylndexOutOfBoundException and will be handled.
 * TornadoVM output:
 * a: [1, 1, 1, 1, 1, 1, 1, 1]
 * b: [2, 2, 2, 2, 2, 2, 2, 2]
 * c: [3, 3, 3, 3, 3, 3, 3, 3]
 * The kernel code shows that the openCL code does not have any exception handle mechanism
 */
public class TryCatchIndex {
    public static void add(int[] a, int[] b, int[] c){
        try {
            for (@Parallel int i = 0; i < c.length + 1; i++) {
                c[i] = a[i] + b[i];
            }
        }catch (Exception ignored){}
    }

    public static void main(String[] args) {

        final int numElements = 8;
        int[] a = new int[numElements];
        int[] b = new int[numElements];
        int[] c = new int[numElements];

        Arrays.fill(a, 1);
        Arrays.fill(b, 2);
        Arrays.fill(c, 0);

        TaskGraph taskGraph = new TaskGraph("s0") //
                .transferToDevice(DataTransferMode.FIRST_EXECUTION, a, b) //
                .task("t0",TryCatchIndex::add, a, b, c) //
                .transferToHost(DataTransferMode.EVERY_EXECUTION, c);

        ImmutableTaskGraph immutableTaskGraph = taskGraph.snapshot();
        TornadoExecutionPlan executor = new TornadoExecutionPlan(immutableTaskGraph);
        executor.execute();

        System.out.println("a: " + Arrays.toString(a));
        System.out.println("b: " + Arrays.toString(b));
        System.out.println("c: " + Arrays.toString(c));
    }
}
