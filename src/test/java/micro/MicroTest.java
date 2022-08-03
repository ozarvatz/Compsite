package micro;

import compoz.Compo;
import compoz.DoXLeaf;
import compoz.DoYLeaf;
import compoz.FailedDoZLeaf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.oz.composite.ChainExcecutionAbs;
import org.oz.composite.IProcessData;
import org.oz.composite.ProcessData;
import org.oz.composite.ProcessUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.assertj.core.api.Assertions.*;
import org.springframework.util.Assert;

import java.util.HashSet;

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
                return "basic test compo!!!";
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
    public void  doXAndYTest() {
        ProcessData pData = new ProcessData();
        Compo compo = new Compo();
        compo.add(new DoXLeaf());
        compo.add(new DoYLeaf());
        boolean retExec = compo.execute(pData);

        Assertions.assertTrue(retExec);
    }

    @Test void doXAsyncYsync() {
        ProcessData pData = new ProcessData();
        Compo compo = new Compo();
        compo.add(new DoXLeaf().asynchronous(true));
        compo.add(new DoYLeaf().asynchronous(true).join(true, 350, true));
        boolean retExec = compo.execute(pData);

        Assertions.assertTrue(retExec);
    }

    @Test void doIfCustomRoleInArray() {
        ProcessData pData = new ProcessData();
        Compo compo = new Compo();
        compo.add(new FailedDoZLeaf());
        compo.add(new DoXLeaf().runIfInCustomOnly(ProcessData.STATUS_CODE, new HashSet<Integer>(){{add(ProcessUtil.PROCESS_FAIL);}}));
        compo.add(new DoYLeaf().asynchronous(true).join(true, 3500, true));

        boolean retExec = compo.execute(pData);

        Assertions.assertTrue(retExec);
    }
}
