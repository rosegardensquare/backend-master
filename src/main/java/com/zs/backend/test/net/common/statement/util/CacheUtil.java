package com.zs.backend.test.net.common.statement.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheUtil {
    private static final Logger log = LoggerFactory.getLogger(CacheUtil.class);

    public CacheUtil() {
    }

    /**
     * 写缓存后多久过期（缓存移除监听）
     *
     * @param duration        持续时间 单位 秒
     * @param maximumSize     最大容量
     * @param removalListener 移除监听
     */
    public static synchronized <T> Cache<String, T> createCaffeineCacheExpireAfterWrite(long duration, long maximumSize, RemovalListener<String, T> removalListener) {
        Cache<String, T> newCache = null;

        try {
            newCache = Caffeine.newBuilder().expireAfterWrite(duration, TimeUnit.SECONDS).maximumSize(maximumSize).removalListener(removalListener).build();
        } catch (Exception var7) {
            log.error("创建公共缓冲区异常", var7);
        }

        return newCache;
    }

    /**
     * 写缓存后多久过期
     *
     * @param duration    持续时间
     * @param maximumSize 最大容量
     */
    public static synchronized <T> Cache<String, T> createCaffeineCacheExpireAfterWrite(long duration, long maximumSize) {
        Cache<String, T> newCache = null;

        try {
            newCache = Caffeine.newBuilder().expireAfterWrite(duration, TimeUnit.MILLISECONDS).maximumSize(maximumSize).build();
        } catch (Exception var6) {
            log.error("创建公共缓冲区异常", var6);
        }

        return newCache;
    }

    /**
     * 读写缓存后多久过期（缓存移除监听）
     *
     * @param duration        持续时间
     * @param maximumSize     最大容量
     * @param removalListener 移除监听
     */
    public static synchronized <T> Cache<String, T> createCaffeineCacheExpireAfterAccess(long duration, long maximumSize, RemovalListener<String, T> removalListener) {
        Cache<String, T> newCache = null;

        try {
            newCache = Caffeine.newBuilder().expireAfterAccess(duration, TimeUnit.SECONDS).maximumSize(maximumSize).removalListener(removalListener).build();
        } catch (Exception var7) {
            log.error("创建公共缓冲区异常", var7);
        }

        return newCache;
    }

    /**
     * 读写缓存后多久过期
     *
     * @param duration    持续时间
     * @param maximumSize 最大容量
     */
    public static synchronized <T> Cache<String, T> createCaffeineCacheExpireAfterAccess(long duration, long maximumSize) {
        Cache<String, T> newCache = null;

        try {
            newCache = Caffeine.newBuilder().expireAfterAccess(duration, TimeUnit.MILLISECONDS).maximumSize(maximumSize).build();
        } catch (Exception var6) {
            log.error("创建公共缓冲区异常", var6);
        }

        return newCache;
    }

    /**
     * 创建默认缓存
     *
     * @param maximumSize 最大容量
     */
    public static synchronized <T> Cache<String, T> createCaffeineCache(long maximumSize) {
        Cache<String, T> newCache = null;

        try {
            newCache = Caffeine.newBuilder().maximumSize(maximumSize).build();
        } catch (Exception var4) {
            log.error("创建公共缓冲区异常", var4);
        }

        return newCache;
    }

    /**
     * 创建默认缓存，移除回调
     *
     * @param maximumSize     最大容量
     * @param removalListener 缓存移除回调
     */
    public static synchronized <T> Cache<String, T> createCaffeineCache(long maximumSize, RemovalListener<String, T> removalListener) {
        Cache<String, T> newCache = null;

        try {
            newCache = Caffeine.newBuilder().maximumSize(maximumSize).removalListener(removalListener).build();
        } catch (Exception var4) {
            log.error("创建公共缓冲区异常", var4);
        }

        return newCache;
    }
}