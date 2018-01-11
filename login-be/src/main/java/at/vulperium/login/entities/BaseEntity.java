package at.vulperium.login.entities;

import java.io.Serializable;

/**
 * Created by 02ub0400 on 11.10.2017.
 */
public interface BaseEntity extends Serializable {

    public abstract Long getId();
    public abstract Integer getOptlock();
}
