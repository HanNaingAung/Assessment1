package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Promotion;
import org.example.repository.PromotionRepository;
import org.testng.Assert;
import org.testng.annotations.Test;;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@SpringBootTest
public class PromotionUnitTest extends BaseUnitTest {

    private Logger testLogger = LogManager.getLogger("testLogs." + getClass());

    @Autowired
    private PromotionRepository repository;

    @Test(groups = {"fetch"})
    public void testSelectAll() throws Exception {
        List<Promotion> results = repository.findAll();
        assertTrue(results.size() > 0, "Records should not be empty.");
        showEntriesOfCollection(results);
    }

    @Test(groups = {"fetch"})
    public void testSelectAllCount() throws Exception {
        long count = repository.count();
        assertTrue(count > 0, "Record count must greater than 0.");
        testLogger.info("Total counts ==> " + count);
    }

    @Test(groups = {"fetch"})
    public void testSelectByPrimaryKey() throws Exception {
        Optional<Promotion> result = repository.findById(1L);
        assertNotNull(result, "Expected record must not be null.");
        testLogger.info("Result ==> " + result);
    }

    @Test(groups = {"fetch"})
    public void testSelectByItemKey() throws Exception {
        Optional<Promotion> result = repository.findByItemId(11L);
        assertNotNull(result, "Expected record must not be null.");
        testLogger.info("Result ==> " + result);
    }

    @Test(groups = {"insert"})
    public void insertSingle() {
        Promotion record = new Promotion();
        record.setId(1L);
        record.setItemId(2L);
        record.setStartDate(new Date());
        record.setEndDate(new Date());
        record.setPromoteFrom("Promote From ABC");
        record.setDescription("New Year Promo");
        record.setCreatedAt(new Date());
        record.setUpdatedAt(new Date());

        Promotion promotion = repository.save(record);
        testLogger.info("Last inserted ID ==> " + promotion.getId());
        testLogger.info("Inserted Record ==> " + promotion);
    }

    @Test(groups = {"insert"})
    public void insertMulti() {
        List<Promotion> promotionList = new ArrayList<>();

        Promotion record = new Promotion();
        record.setId(1L);
        record.setItemId(2L);
        record.setStartDate(new Date());
        record.setEndDate(new Date());
        record.setPromoteFrom("Promote From ABC");
        record.setDescription("New Year Promo");
        record.setCreatedAt(new Date());
        record.setUpdatedAt(new Date());

        Promotion record2 = new Promotion();
        record2.setId(2L);
        record2.setItemId(3L);
        record2.setStartDate(new Date());
        record2.setEndDate(new Date());
        record2.setPromoteFrom("Promote From Star Group");
        record2.setDescription("New Year Promo");
        record2.setCreatedAt(new Date());
        record2.setUpdatedAt(new Date());


        promotionList.add(record);
        promotionList.add(record2);

        List<Promotion> resultList = repository.saveAll(promotionList);
        testLogger.info("Inserted Record ==> " + resultList);
    }

    @Test(groups = {"update"})
    public void testSingleRecordUpdate() {
        Promotion record = new Promotion();
        record.setId(2L);
        record.setItemId(4L);
        record.setStartDate(new Date());
        record.setEndDate(new Date());
        record.setPromoteFrom("Promote From Big C");
        record.setDescription("Special Promo");
        record.setCreatedAt(new Date());
        record.setUpdatedAt(new Date());


        Promotion result = repository.save(record);
        testLogger.info("Updated result is = " + result);
    }

    @Test(groups = {"delete"})
    public void testDeleteByPrimaryKey()  {
        try{
            repository.deleteById(3L);
            testLogger.info("Delete process successfully");

        }catch (Exception e){
            testLogger.error("Delete process failed !!", e);
            Assert.fail("Delete process failed: " + e.getMessage());
        }
    }

}
