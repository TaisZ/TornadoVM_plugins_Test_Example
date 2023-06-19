package recursion;

import uk.ac.manchester.tornado.api.ImmutableTaskGraph;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;

public class RecursiveTask {
    public static void recursion(int a, int b){
        if(a == 0) b += 1;

        for (@Parallel int i = 0; i<a; i++){
            recursion(a-1, b);
        }
    }

    public static void main(String[] args) {
        int a = 3;
        int b = 0;

        TaskGraph taskGraph = new TaskGraph("s0") //
                .transferToDevice(DataTransferMode.FIRST_EXECUTION, a) //
                .task("t0", RecursiveTask::recursion, a, b) //
                .transferToHost(DataTransferMode.EVERY_EXECUTION, b);

        ImmutableTaskGraph immutableTaskGraph = taskGraph.snapshot();
        TornadoExecutionPlan executor = new TornadoExecutionPlan(immutableTaskGraph);
        executor.execute();

    }
}
