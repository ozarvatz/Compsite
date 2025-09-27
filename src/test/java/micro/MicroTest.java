package micro;

import org.oz.composite.TestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.oz.composite.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public class MicroTest {

    @Autowired
    @Qualifier("DoXLeaf")
    public IChainExecution doXLeaf;
    @Autowired
    @Qualifier("DoYLeaf")
    public IChainExecution doYLeaf;
    @Autowired
    @Qualifier("ExcecutorCompo")
    public IChainExecution excecutorCompo;

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

    @Test
    public void vasicSpringTest() {
        ProcessData pData = new ProcessData();
        excecutorCompo.add(doXLeaf);
        excecutorCompo.add(doYLeaf);
        Assertions.assertTrue(excecutorCompo.execute(pData));
        Assertions.assertTrue(pData.getStatusCode() == ProcessUtil.OK);
    }

}
