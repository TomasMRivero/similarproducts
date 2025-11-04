package com.tmartinez.similarproducts.infrastructure.util;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RetryProductDetailTracker {
    private final Set<String> retrying = ConcurrentHashMap.newKeySet();

    public boolean startRetry(String rootId) {
        return retrying.add(rootId);
    }

    public boolean endRetry(String rootId) {
        return retrying.remove(rootId);
    }

    public boolean isRetrying(String rootId) {
        return retrying.contains(rootId);
    }
}
