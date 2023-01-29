module dorkbox.objectpool {
    exports dorkbox.objectPool;

    requires transitive dorkbox.updates;

    requires transitive com.conversantmedia.disruptor;

    requires transitive kotlinx.coroutines.core.jvm;
    requires transitive kotlin.stdlib;
}
