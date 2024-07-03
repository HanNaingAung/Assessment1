package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.util.Collection;

public class BaseUnitTest extends AbstractTestNGSpringContextTests {
    private static final Logger testLogger = LogManager.getLogger("testLogs." + BaseUnitTest.class.getName());

    @BeforeMethod
    public void beforeMethod(Method method) {
        testLogger.info("***** Unit-TEST : Testing method '" + method.getName() + "' has started. *****");
        MockitoAnnotations.initMocks(this);
    }

    @AfterMethod
    public void afterMethod(Method method) {
        testLogger.info("----- Unit-TEST : Testing method '" + method.getName() + "' has finished. -----");
    }

    protected <T> void showEntriesOfCollection(Collection<T> collection) {
        if (collection != null) {
            for (Object obj : collection) {
                testLogger.info(" >>> " + obj.toString());
            }
        }
    }
}
