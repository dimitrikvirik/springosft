package git.dimitrikvirik.springsoft.user.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import git.dimitrikvirik.springsoft.user.model.entity.User;
import lombok.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserDTO implements IdentifiedDataSerializable {

    public static final int FACTORY_ID = 1000;
    public static final int CLASS_ID = 1;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("firstname")
    private String firstname;

    @JsonProperty("lastname")
    private String lastname;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("createdAt")
    private String createdAt;

    public static UserDTO fromEntity(User user) {

        return UserDTO.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(user.getCreatedAt()))
                .build();
    }

    @Override
    @JsonIgnore
    public  int getFactoryId() {
        return 1000;
    }

    @Override
    @JsonIgnore
    public int getClassId() {
        return 1;
    }

    @Override
    public void writeData(ObjectDataOutput objectDataOutput) throws IOException {
        objectDataOutput.writeLong(id);
        objectDataOutput.writeString(firstname);
        objectDataOutput.writeString(lastname);
        objectDataOutput.writeString(username);
        objectDataOutput.writeString(email);
        objectDataOutput.writeString(createdAt);
    }

    @Override
    public void readData(ObjectDataInput objectDataInput) throws IOException {
        id = objectDataInput.readLong();
        firstname = objectDataInput.readString();
        lastname = objectDataInput.readString();
        username = objectDataInput.readString();
        email = objectDataInput.readString();
        createdAt = objectDataInput.readString();
    }
}
