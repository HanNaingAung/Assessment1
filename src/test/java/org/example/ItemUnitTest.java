package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Item;
import org.example.repository.ItemRepository;
import org.testng.Assert;
import org.testng.annotations.Test;;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@SpringBootTest
public class ItemUnitTest extends BaseUnitTest {

    private Logger testLogger = LogManager.getLogger("testLogs." + getClass());

    @Autowired
    private ItemRepository repository;

    @Test(groups = {"fetch"})
    public void testSelectAll() throws Exception {
        List<Item> results = repository.findAll();
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
        Optional<Item> result = repository.findById(1L);
        assertNotNull(result, "Expected record must not be null.");
        testLogger.info("Result ==> " + result);
    }


    @Test(groups = {"insert"})
    public void insertSingle() {
        Item record = new Item();
        record.setId(4L);
        record.setName("Ve Ve");
        record.setDuration(30);
        record.setDistance(4.5);
        record.setRating(4.8);
        record.setDeliveryFee(50.0);
        record.setCategories(Arrays.asList("ABC","DEF"));
        record.setImageUrl("url");
        record.setCreatedAt(new Date());
        record.setUpdatedAt(new Date());

        Item Item = repository.save(record);
        testLogger.info("Last inserted ID ==> " + Item.getId());
        testLogger.info("Inserted Record ==> " + Item);
    }

    @Test(groups = {"insert"})
    public void insertMulti() {
        List<Item> ItemList = new ArrayList<>();

        Item record = new Item();
        record.setId(1L);
        record.setId(1L);
        record.setName("Coca Cola");
        record.setDuration(30);
        record.setDistance(4.5);
        record.setRating(4.8);
        record.setDeliveryFee(50.0);
        record.setCategories(Arrays.asList("Food","Drink"));
        record.setImageUrl("url");
        record.setCreatedAt(new Date());
        record.setUpdatedAt(new Date());

        Item record2 = new Item();
        record2.setId(2L);
        record2.setName("Good Morning");
        record2.setDuration(15);
        record2.setDistance(2.5);
        record2.setRating(5.0);
        record2.setDeliveryFee(20.0);
        record2.setCategories(Arrays.asList("Food"));
        record2.setImageUrl("url");
        record2.setCreatedAt(new Date());
        record2.setUpdatedAt(new Date());


        ItemList.add(record);
        ItemList.add(record2);

        List<Item> resultList = repository.saveAll(ItemList);
        testLogger.info("Inserted Record ==> " + resultList);
    }

    @Test(groups = {"update"})
    public void testSingleRecordUpdate() {
        Item record = new Item();
        record.setId(2L);
        record.setName("Good Morning");
        record.setDuration(15);
        record.setDistance(2.5);
        record.setRating(5.0);
        record.setDeliveryFee(20.0);
        record.setCategories(Arrays.asList("Food"));
        record.setImageUrl("url");
        record.setCreatedAt(new Date());
        record.setUpdatedAt(new Date());


        Item result = repository.save(record);
        testLogger.info("Updated result is = " + result);
    }

    @Test(groups = {"delete"})
    public void testDeleteByPrimaryKey()  {
        try{
            repository.deleteById(4L);
            testLogger.info("Delete process successfully");

        }catch (Exception e){
            testLogger.error("Delete process failed !!", e);
            Assert.fail("Delete process failed: " + e.getMessage());
        }
    }

}
