package git.dimitrikvirik.springsoft.user.model;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import git.dimitrikvirik.springsoft.common.services.CacheFactory;
import git.dimitrikvirik.springsoft.user.model.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserDataSerializableFactory implements DataSerializableFactory,  CacheFactory {
    public static final int FACTORY_ID = 1000;

    @Override
    public IdentifiedDataSerializable create(int typeId) {
        if (typeId == UserDTO.CLASS_ID) {
            return new UserDTO();
        }
        return null;
    }

    @Override
    public int getFactoryId() {
        return FACTORY_ID;
    }

    @Override
    public DataSerializableFactory newInstance() {
        return new UserDataSerializableFactory();
    }
}