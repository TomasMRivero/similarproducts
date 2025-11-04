package com.tmartinez.similarproducts.infrastructure.util;

import java.util.Set;

public interface CacheDependencyTracker {
    public void register(String rootProductId, Set<String> similarProductIds);
    public Set<String> findDependentProducts(String productId);
    public void removeProduct(String productId);
}
