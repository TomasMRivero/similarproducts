package com.tmartinez.similarproducts.infrastructure.util;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component("memoryCacheDependencyTracker")
public class MemoryCacheDependencyTracker implements CacheDependencyTracker {
    private final ConcurrentHashMap<String, Set<String>> dependencyMap = new ConcurrentHashMap<>();

    @Override
    public void register(String rootProductId, Set<String> similarProductIds) {


        similarProductIds.forEach(productId -> {
            dependencyMap.compute(productId, (root, existing) -> {
                Set<String> set = existing != null ? existing : ConcurrentHashMap.newKeySet();
                set.add(rootProductId);
                return set;
            });
        });
    }

    @Override
    public Set<String> findDependentProducts(String productId) {
        return dependencyMap.getOrDefault(productId, Collections.emptySet());
    }

    @Override
    public void removeProduct(String productId) {
        dependencyMap.remove(productId);
    }
}
