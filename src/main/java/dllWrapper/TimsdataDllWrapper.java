package dllWrapper;

//import com.sun.jna.Library;


import com.sun.jna.*;

public class TimsdataDllWrapper {
    public interface TimsdataDll extends Library {
        TimsdataDll INSTANCE = (TimsdataDll)Native.loadLibrary("C:\\Users\\Anton\\Documents\\sqliteReader\\lib\\timsdata\\timsdata.dll", TimsdataDll.class);

    }

    public static void main(String[] args) {

        TimsdataDll sdll = TimsdataDll.INSTANCE;

        System.out.println("test");



    }
}
