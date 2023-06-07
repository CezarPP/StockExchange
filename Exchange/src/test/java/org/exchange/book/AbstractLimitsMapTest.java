package org.exchange.book;

import org.common.fix.order.Side;
import org.exchange.book.limitsMap.LimitsMap;
import org.exchange.book.limitsMap.LimitsTreeMap;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class AbstractLimitsMapTest {
    static Random random = new Random();
    static final int cntToInsert = (1 << 16);

    protected abstract LimitsMap<Limit> getLimitsMapDescending();
    protected abstract LimitsMap<Limit> getLimitsMapAscending();

    @Test
    void testInsertAndGet() {
        Map<Float, Limit> inserted = new TreeMap<>();
        LimitsMap<Limit> limitsMap = getLimitsMapAscending();

        for (int i = 0; i < cntToInsert; i++) {
            Limit limit = new Limit(getRandomPrice(), (random.nextBoolean()) ? Side.BUY : Side.SELL);
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
        LimitsMap<Limit> limitsMap = getLimitsMapAscending();
        TreeMap<Float, Limit> limitsMapCorrect = new TreeMap<>();
        final int cntToDelete = cntToInsert / 3;
        for (int i = 0; i < cntToInsert; i++) {
            Limit limit = new Limit(getRandomPrice(), (random.nextBoolean()) ? Side.BUY : Side.SELL);
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

    static Float getRandomPrice() {
        float price = random.nextFloat() * 10000;
        int temp = (int) (price * 100);
        price = (float) temp / 100;
        return price;
    }

    // Also tests insert and delete
    @Test
    void testGetFirstAscending() {
        List<Limit> inserted = new ArrayList<>();
        LimitsMap<Limit> limitsMap = getLimitsMapAscending();
        LimitsMap<Limit> limitsMapCorrect = new LimitsTreeMap<>();
        final int cntToDelete = cntToInsert / 3;

        for (int i = 0; i < cntToInsert; i++) {
            Limit limit = new Limit(getRandomPrice(), (random.nextBoolean()) ? Side.BUY : Side.SELL);
            limitsMap.put(limit.getPrice(), limit);
            limitsMapCorrect.put(limit.getPrice(), limit);
            inserted.add(limit);
        }

        for (int i = 0; i < cntToDelete; i++) {
            limitsMap.remove(inserted.get(i).getPrice());
            limitsMapCorrect.remove(inserted.get(i).getPrice());

            Map.Entry<Float, Limit> firstLimitCorrect = limitsMapCorrect.firstEntry();
            Map.Entry<Float, Limit> firstLimit = limitsMap.firstEntry();
            if (firstLimitCorrect == null)
                continue;
            if (!Objects.equals(firstLimitCorrect.getKey(), firstLimit.getKey())) {
                for (Limit ins : inserted)
                    System.out.println(ins.getPrice());
            }
            assertEquals(firstLimitCorrect.getKey(), firstLimit.getKey());
            assertEquals(firstLimitCorrect.getValue().getSide(), firstLimit.getValue().getSide());
            assertEquals(firstLimitCorrect.getValue().getPrice(), firstLimit.getValue().getPrice());
        }
    }

    @Test
    void testGetFirstDescending() {
        List<Limit> inserted = new ArrayList<>();
        LimitsMap<Limit> limitsMap = getLimitsMapDescending();
        LimitsMap<Limit> limitsMapCorrect = new LimitsTreeMap<>(true);
        final int cntToDelete = cntToInsert / 3;

        for (int i = 0; i < cntToInsert; i++) {
            Limit limit = new Limit(getRandomPrice(), (random.nextBoolean()) ? Side.BUY : Side.SELL);
            limitsMap.put(limit.getPrice(), limit);
            limitsMapCorrect.put(limit.getPrice(), limit);
            inserted.add(limit);
        }

        for (int i = 0; i < cntToDelete; i++) {
            limitsMap.remove(inserted.get(i).getPrice());
            limitsMapCorrect.remove(inserted.get(i).getPrice());

            Map.Entry<Float, Limit> firstLimitCorrect = limitsMapCorrect.firstEntry();
            Map.Entry<Float, Limit> firstLimit = limitsMap.firstEntry();
            if (firstLimitCorrect == null)
                continue;
            assertEquals(firstLimitCorrect.getKey(), firstLimit.getKey());
            assertEquals(firstLimitCorrect.getValue().getSide(), firstLimit.getValue().getSide());
            assertEquals(firstLimitCorrect.getValue().getPrice(), firstLimit.getValue().getPrice());
        }
    }

    // Also tests insertion and deletion
    @Test
    void getFirstNAscending() {
        List<Limit> inserted = new ArrayList<>();
        LimitsMap<Limit> limitsMap = getLimitsMapAscending();
        LimitsMap<Limit> limitsMapCorrect = new LimitsTreeMap<>();
        final int cntToDelete = cntToInsert / 3;

        for (int i = 0; i < cntToInsert; i++) {
            Limit limit = new Limit(getRandomPrice(), (random.nextBoolean()) ? Side.BUY : Side.SELL);
            limitsMap.put(limit.getPrice(), limit);
            limitsMapCorrect.put(limit.getPrice(), limit);
            inserted.add(limit);
        }

        for (int i = 0; i < cntToDelete; i++) {
            limitsMap.remove(inserted.get(i).getPrice());
            limitsMapCorrect.remove(inserted.get(i).getPrice());

            List<Limit> firstLimitCorrect = limitsMapCorrect.getFirstN(20);
            List<Limit> firstLimit = limitsMap.getFirstN(20);
            if (firstLimitCorrect == null)
                continue;
            assertEquals(firstLimitCorrect.size(), firstLimit.size());
            for (int j = 0; j < firstLimitCorrect.size(); j++) {
                Limit expected = firstLimitCorrect.get(j);
                Limit actual = firstLimit.get(j);
                assertEquals(expected.getPrice(), actual.getPrice());
                assertEquals(expected.getSide(), actual.getSide());
            }
        }
    }

    // Also tests insertion and deletion
    @Test
    void getFirstNDescending() {
        List<Limit> inserted = new ArrayList<>();
        LimitsMap<Limit> limitsMap = getLimitsMapDescending();
        LimitsMap<Limit> limitsMapCorrect = new LimitsTreeMap<>(true);
        final int cntToDelete = cntToInsert / 3;

        for (int i = 0; i < cntToInsert; i++) {
            Limit limit = new Limit(getRandomPrice(), (random.nextBoolean()) ? Side.BUY : Side.SELL);
            limitsMap.put(limit.getPrice(), limit);
            limitsMapCorrect.put(limit.getPrice(), limit);
            inserted.add(limit);
        }

        for (int i = 0; i < cntToDelete; i++) {
            limitsMap.remove(inserted.get(i).getPrice());
            limitsMapCorrect.remove(inserted.get(i).getPrice());

            List<Limit> firstLimitCorrect = limitsMapCorrect.getFirstN(20);
            List<Limit> firstLimit = limitsMap.getFirstN(20);
            if (firstLimitCorrect == null)
                continue;
            assertEquals(firstLimitCorrect.size(), firstLimit.size());
            for (int j = 0; j < firstLimitCorrect.size(); j++) {
                Limit expected = firstLimitCorrect.get(j);
                Limit actual = firstLimit.get(j);
                assertEquals(expected.getPrice(), actual.getPrice());
                assertEquals(expected.getSide(), actual.getSide());
            }
        }
    }
}
