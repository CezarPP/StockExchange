package org.exchange.book;

import org.common.fix.order.Side;
import org.exchange.book.limitsMap.LimitsMap;
import org.exchange.book.limitsMap.LimitsTreeMap;
import org.exchange.book.limitsMap.art.LimitsArtMap;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LimitsArtMapTest {
    static Random random = new Random();
    static final int cntToInsert = 8192;

    @Test
    void testInsertAndGet() {
        Map<Float, Limit> inserted = new TreeMap<>();
        LimitsMap<Limit> limitsMap = new LimitsArtMap<>();

        for (int i = 0; i < cntToInsert; i++) {
            Limit limit = new Limit(random.nextFloat() * 10000, (random.nextBoolean()) ? Side.BUY : Side.SELL);
            limitsMap.put(limit.getPrice(), limit);
            inserted.put(limit.getPrice(), limit);
        }

        for (Map.Entry<Float, Limit> limit : inserted.entrySet()) {
            Limit seachedLimit = limitsMap.get(limit.getKey());
            assertNotNull(seachedLimit);
            assertEquals(limit.getValue().getPrice(), seachedLimit.getPrice());
            assertEquals(limit.getValue().getSide(), seachedLimit.getSide());
        }
    }

    @Test
    void testInsertDeleteAndGet() {
        List<Limit> inserted = new ArrayList<>();
        LimitsMap<Limit> limitsMap = new LimitsArtMap<>();
        TreeMap<Float, Limit> limitsMapCorrect = new TreeMap<>();
        final int cntToDelete = cntToInsert / 3;

        for (int i = 0; i < cntToInsert; i++) {
            float price = random.nextFloat() * 10000;
            int temp = (int) (price * 100);
            price = (float) temp / 100;
            Limit limit = new Limit(price, (random.nextBoolean()) ? Side.BUY : Side.SELL);
            limitsMap.put(limit.getPrice(), limit);
            limitsMapCorrect.put(limit.getPrice(), limit);
            inserted.add(limit);
        }

        for (int i = 0; i < cntToDelete; i++) {
            limitsMap.remove(inserted.get(i).getPrice());
            limitsMapCorrect.remove(inserted.get(i).getPrice());
        }

        for (Map.Entry<Float, Limit> entry : limitsMapCorrect.entrySet()) {
            Limit limitCorrect = entry.getValue();
            Limit limit = limitsMap.get(entry.getKey());

            assertNotNull(limit);
            assertEquals(limitCorrect.getSide(), limit.getSide());
            assertEquals(limitCorrect.getPrice(), limit.getPrice());
        }
    }
}
