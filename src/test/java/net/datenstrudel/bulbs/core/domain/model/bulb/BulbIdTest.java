package net.datenstrudel.bulbs.core.domain.model.bulb;

import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BulbIdTest {

    @Test
    public void serialize_deserialize() throws Exception {
        BulbId bId = new BulbId(new BulbBridgeId(UUID.randomUUID().toString()), 123);
        String serialized = bId.serialize();
        BulbId res = BulbId.fromSerialized(serialized);
        assertThat(res, is(bId));
    }
}