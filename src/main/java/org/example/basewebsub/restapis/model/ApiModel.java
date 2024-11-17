package org.example.basewebsub.restapis.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Hieunt
 */
@Data
public class ApiModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1079859015838391669L;

    private String grant_type;
    private String destination;
    private String completecollChildid;
    private String loanReference;
    private String loanId;
}