package com.maximsachok.authoridentification.entitys;


import java.io.Serializable;
import java.math.BigInteger;

public class AuthorProjectCompositeId implements Serializable {
    private BigInteger expertId;

    private BigInteger projectId;
}
