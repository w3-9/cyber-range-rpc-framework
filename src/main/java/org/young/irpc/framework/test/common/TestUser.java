package org.young.irpc.framework.test.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName TestUser
 * @Description TODO
 * @Author young
 * @Date 2023/2/24 下午7:20
 * @Version 1.0
 **/
@Data
public class TestUser implements Serializable {
    private static final long serialVersionUID = -1124101350467313888L;

    private int age;

    private String name;

    private String address;

    private String mail;

}
