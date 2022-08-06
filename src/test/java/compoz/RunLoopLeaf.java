package compoz;

import org.oz.composite.ChainExcecutionAbs;
import org.oz.composite.IProcessData;
import org.oz.composite.ProcessUtil;

public class RunLoopLeaf extends ChainExcecutionAbs {
    @Override
    public String getDescription() {
        return "run element in loop";
    }

    @Override
    public boolean run(IProcessData pData) {
        Object element = this.poll(pData);
        try { Thread.sleep(100); } catch (Exception e) { e.printStackTrace(); }
        pData.addMessage(ProcessUtil.PROCESS_INFO,
                String.format("current thread %s, run element %s, return true",
                        Thread.currentThread().getName(), element.toString()));
        return true;
    }
}
