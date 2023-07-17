package taskmethod;

import uk.ac.manchester.tornado.api.annotations.Parallel;

public class sout{
    public static void add(int[] a, int[] b, int[] c) {
        for (@Parallel int i = 0; i < c.length; i++) {
            c[i] = a[i] + b[i];
        }
    }
}
