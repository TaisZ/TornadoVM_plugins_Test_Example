package datatype;

import uk.ac.manchester.tornado.api.ImmutableTaskGraph;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;

import java.util.ArrayList;

public class ArrayListAdd {
    public static void arrayList_add(ArrayList<Integer> a, ArrayList<Integer> b, ArrayList<Integer> c){
        for (@Parallel int i = 0; i<a.size(); i++){
            c.set(i, a.get(i) + b.get(i));
        }
    }

    public static void main(String[] args) {
        ArrayList<Integer> a = new ArrayList<Integer>();
        ArrayList<Integer> b = new ArrayList<Integer>();
        ArrayList<Integer> c = new ArrayList<Integer>();

        for (int i = 0; i<8; i++){
            a.add(i);
            b.add(i);
        }

        TaskGraph taskGraph = new TaskGraph("s0") //
                .transferToDevice(DataTransferMode.FIRST_EXECUTION, a, b) //
                .task("t0", ArrayListAdd::arrayList_add, a, b, c) //
                .transferToHost(DataTransferMode.EVERY_EXECUTION, c);

        ImmutableTaskGraph immutableTaskGraph = taskGraph.snapshot();
        TornadoExecutionPlan executor = new TornadoExecutionPlan(immutableTaskGraph);
        executor.execute();
    }
}
