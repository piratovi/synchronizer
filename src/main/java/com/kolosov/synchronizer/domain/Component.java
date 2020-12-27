package com.kolosov.synchronizer.domain;

import java.util.stream.Stream;

public interface Component {

    Stream<Sync> getNestedSyncs();

    void removeFromParent();
}
