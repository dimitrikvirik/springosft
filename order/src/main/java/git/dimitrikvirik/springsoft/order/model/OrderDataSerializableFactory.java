package git.dimitrikvirik.springsoft.order.model;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import git.dimitrikvirik.springsoft.order.model.dto.OrderDTO;

public class OrderDataSerializableFactory implements DataSerializableFactory {
    public static final int FACTORY_ID = 2000;

    @Override
    public IdentifiedDataSerializable create(int typeId) {
        if (typeId == OrderDTO.CLASS_ID) {
            return new OrderDTO();
        }
        return null;
    }
}