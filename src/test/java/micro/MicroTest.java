package micro;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.oz.composite.ChainExcecutionAbs;
import org.oz.composite.IProcessData;
import org.oz.composite.ProcessData;
import org.oz.composite.ProcessUtil;
import org.springframework.boot.test.context.SpringBootTest;
import org.assertj.core.api.Assertions.*;
import org.springframework.util.Assert;

@SpringBootTest(classes=MicroTest.class)
public class MicroTest {

    @Test
    public void basicTest() {
        ProcessData pData = new ProcessData();
        ChainExcecutionAbs executor = new ChainExcecutionAbs() {
            @Override
            public boolean run(IProcessData pData) {
                return true;
            }

            @Override
            public String getDescription() {
                return null;
            }
        };
        executor.add(new ChainExcecutionAbs() {
            @Override
            public boolean run(IProcessData pData) {
                pData.addMessage(ProcessUtil.OK, "success do somthing");
                return true;
            }

            @Override
            public String getDescription() {
                return "process that do something";
            }
        });
        Assertions.assertTrue(executor.execute(pData));
        Assertions.assertTrue(pData.getStatusCode() == ProcessUtil.OK);
    }


}
