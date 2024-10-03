package git.dimitrikvirik.springsoft.user.model;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import git.dimitrikvirik.springsoft.user.model.dto.UserDTO;

public class UserDataSerializableFactory implements DataSerializableFactory {
    public static final int FACTORY_ID = 1000;

    @Override
    public IdentifiedDataSerializable create(int typeId) {
        if (typeId == UserDTO.CLASS_ID) {
            return new UserDTO();
        }
        return null;
    }
}