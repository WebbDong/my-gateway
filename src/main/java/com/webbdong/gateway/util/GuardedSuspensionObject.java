package com.webbdong.gateway.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

/**
 * @author Webb Dong
 * @description: Guarded Suspension模式
 * @date 2021-01-28 12:00 PM
 * @param <T>
 */

public class GuardedSuspensionObject<T> {

    /**
     * 受保护的对象
     */
    private T obj;

    private final Lock lock = new ReentrantLock();

    private final Condition done = lock.newCondition();

    private int timeout;

    private TimeUnit timeoutUnit;

    private final static Map<Object, GuardedSuspensionObject> goMap = new ConcurrentHashMap<>();

    /**
     * 创建GuardedObject
     * @param key
     * @param timeout
     * @param timeoutUnit
     * @param <K>
     * @return
     */
    public static <K> GuardedSuspensionObject create(K key, int timeout, TimeUnit timeoutUnit) {
        GuardedSuspensionObject go = new GuardedSuspensionObject();
        go.timeout = timeout;
        go.timeoutUnit = timeoutUnit;
        goMap.put(key, go);
        return go;
    }

    /**
     * 触发事件
     * @param key
     * @param obj
     * @param <K>
     * @param <T>
     */
    public static <K, T> void fireEvent(K key, T obj) {
        final GuardedSuspensionObject go = goMap.remove(key);
        if (go != null) {
            go.onChange(obj);
        }
    }

    /**
     * 获取受保护对象
     * @param p
     * @return
     */
    public T get(Predicate<T> p) {
        try {
            lock.lock();
            while (!p.test(obj)) {
                // 等待指定的时间后释放锁
                done.await(timeout, timeoutUnit);
            }
            return obj;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 事件通知
     * @param obj
     */
    private void onChange(T obj) {
        try {
            lock.lock();
            this.obj = obj;
            done.signalAll();
        } finally {
            lock.unlock();
        }
    }

}

