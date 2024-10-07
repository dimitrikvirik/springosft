package git.dimitrikvirik.springsoft.common.services;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public interface CacheFactory {

    int getFactoryId();

    DataSerializableFactory newInstance();

}
