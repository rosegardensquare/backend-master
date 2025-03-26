package com.zs.backend.test.net.common.statement.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Tianyun
 * @description
 * @create 2024-11-07 11:17
 **/
@Data
@Accessors(chain = true)
public class CertVo {

    /**
     * 不赋值时 以cert/client-cert.pemh或cert/server-cert.pem为默认值
     */
    private String certPath;

    /**
     * 不赋值时 以cert/client-key.pem或cert/server-key.pem为默认值
     */
    private String keyPath;

    /**
     * 不赋值时 以cert/client-ca.pem为默认值
     */
    private String caPath;
}
