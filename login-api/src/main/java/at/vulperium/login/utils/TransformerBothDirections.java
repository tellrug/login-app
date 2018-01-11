package at.vulperium.login.utils;

/**
 * Created by 02ub0400 on 14.09.2017.
 */
public interface TransformerBothDirections<S, T> extends Transformer<S, T> {

    S transformInverse(T source);

    S transformInverse(T source, S target);
}
