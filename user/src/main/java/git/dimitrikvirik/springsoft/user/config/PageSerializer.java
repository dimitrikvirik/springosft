package git.dimitrikvirik.springsoft.user.config;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageSerializer implements StreamSerializer<Page<?>> {

    @Override
    public void write(ObjectDataOutput out, Page<?> page) throws IOException {
        out.writeInt(page.getNumber());
        out.writeInt(page.getSize());
        out.writeLong(page.getTotalElements());
        out.writeInt(page.getContent().size());
        for (Object item : page.getContent()) {
            out.writeObject(item);
        }
    }

    @Override
    public Page<?> read(ObjectDataInput in) throws IOException {
        int number = in.readInt();
        int size = in.readInt();
        long totalElements = in.readLong();
        int contentSize = in.readInt();
        List<Object> content = new ArrayList<>(contentSize);
        for (int i = 0; i < contentSize; i++) {
            content.add(in.readObject());
        }
        return new PageImpl<>(content, PageRequest.of(number, size), totalElements);
    }

    @Override
    public int getTypeId() {
        return 1001;  // Choose a unique ID
    }

    @Override
    public void destroy() {
    }
}