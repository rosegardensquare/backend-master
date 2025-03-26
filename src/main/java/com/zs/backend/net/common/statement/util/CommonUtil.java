package com.zs.backend.net.common.statement.util;


import cn.hutool.core.util.StrUtil;
import com.zs.backend.net.common.statement.vo.CertVo;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Tianyun
 * @description
 * @create 2024-11-05 16:19
 **/
@Slf4j
public class CommonUtil {

    private static final String DEFAULT_CLIENT_CERT_PATH = "/cert/client-cert.pem";
    private static final String DEFAULT_CLIENT_KEY_PATH = "/cert/client-key.pem";
    private static final String DEFAULT_CA_PATH = "/cert/ca.pem";

    public static byte[] toByteArray(InputStream is) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int n;
            while (-1 != (n = is.read(buffer))) {
                output.write(buffer, 0, n);
            }
            return output.toByteArray();
        }
    }

    public static SslContext getClientSslCtx(CertVo certVo) throws IOException {

        InputStream clientCertStream = certVo != null && StrUtil.isNotBlank(certVo.getCertPath()) ?
            new FileInputStream(certVo.getCertPath()) : CommonUtil.class.getResourceAsStream(DEFAULT_CLIENT_CERT_PATH);

        InputStream clientKeyStream = certVo != null && StrUtil.isNotBlank(certVo.getKeyPath()) ?
            new FileInputStream(certVo.getKeyPath()) : CommonUtil.class.getResourceAsStream(DEFAULT_CLIENT_KEY_PATH);

        InputStream caCertStream = certVo != null && StrUtil.isNotBlank(certVo.getCaPath()) ?
            new FileInputStream(certVo.getCaPath()) : CommonUtil.class.getResourceAsStream(DEFAULT_CA_PATH);

        byte[] clientCertBytes = CommonUtil.toByteArray(clientCertStream);
        byte[] clientKeyBytes = CommonUtil.toByteArray(clientKeyStream);
        byte[] caCertBytes = CommonUtil.toByteArray(caCertStream);

        return SslContextBuilder.forClient()
            .keyManager(new ByteArrayInputStream(clientCertBytes), new ByteArrayInputStream(clientKeyBytes))
            .trustManager(new ByteArrayInputStream(caCertBytes))
            .build();
    }



}
