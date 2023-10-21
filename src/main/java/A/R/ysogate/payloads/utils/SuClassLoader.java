package A.R.ysogate.payloads.utils;

/**
 * @author su18
 */
public class SuClassLoader extends ClassLoader {

    public SuClassLoader() {
        super(Thread.currentThread().getContextClassLoader());
    }
}
