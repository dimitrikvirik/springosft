package git.dimitrikvirik.springsoft.order.model;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import git.dimitrikvirik.springsoft.common.services.CacheFactory;
import git.dimitrikvirik.springsoft.order.model.dto.OrderDTO;
import org.springframework.stereotype.Component;

@Component
public class OrderDataSerializableFactory implements DataSerializableFactory, CacheFactory {
    public static final int FACTORY_ID = 2000;

    @Override
    public IdentifiedDataSerializable create(int typeId) {
        if (typeId == OrderDTO.CLASS_ID) {
            return new OrderDTO();
        }
        return null;
    }


    @Override
    public int getFactoryId() {
        return FACTORY_ID;
    }

    @Override
    public DataSerializableFactory newInstance() {
        return new OrderDataSerializableFactory();
    }
}