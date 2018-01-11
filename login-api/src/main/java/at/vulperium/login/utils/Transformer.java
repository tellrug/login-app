package at.vulperium.login.utils;

/**
 * Created by 02ub0400 on 10.07.2017.
 */
public interface Transformer<S, T> {


    T transform(S source);

    T transform(S source, T target);
}
