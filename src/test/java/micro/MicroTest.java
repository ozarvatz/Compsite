package micro;

import compoz.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.oz.composite.ChainExcecutionAbs;
import org.oz.composite.IProcessData;
import org.oz.composite.ProcessData;
import org.oz.composite.ProcessUtil;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

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
        compo.add(new DoYLeaf().asynchronous(true).join(true));
        boolean retExec = compo.execute(pData);

        Assertions.assertTrue(retExec);
    }

    @Test
    public void AsyncJoinWithInterupt() {
        ProcessData pData = new ProcessData();
        Compo compo = new Compo();
        compo.add(new DoXLeaf().asynchronous(true));
        compo.add(new DoYLeaf().asynchronous(true).join(true, 250, true));
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

    @Test
    public void doLoopTest(){
        ProcessData pData = new ProcessData();
        pData.put("elements", new ConcurrentLinkedQueue<String>(){{
            add("moshe");add("eliran");add("igor");add("lidar");
        }});
        Compo compo = new Compo();
        compo.add(new RunLoopLeaf().foreach("elements", false, false, 0));
//        compo.add(new DoYLeaf().asynchronous(true).join(true, 3500, true));

        boolean retExec = compo.execute(pData);

        Assertions.assertTrue(retExec);
    }

    @Test
    public void doAsyncLoopTest(){
        ProcessData pData = new ProcessData();
        pData.put("elements", new ConcurrentLinkedQueue<String>(){{
            add("moshe");add("eliran");add("igor");add("lidar");
        }});
        Compo compo = new Compo();
        compo.add(new RunLoopLeaf()
                .foreach("elements", false, false, 0)
                .asynchronous(true)
                .join(true)
        );
//        compo.add(new DoYLeaf().asynchronous(true).join(true, 3500, true));

        boolean retExec = compo.execute(pData);

        Assertions.assertTrue(retExec);
    }


}
