package compoz;

import org.oz.composite.ChainExcecutionAbs;
import org.oz.composite.IProcessData;
import org.oz.composite.ProcessUtil;

public class DoYLeaf extends ChainExcecutionAbs {
    @Override
    public boolean run(IProcessData pData) {
        try { Thread.sleep(200); } catch (Exception e) { e.printStackTrace(); }
        pData.addMessage(ProcessUtil.OK, "Do Y staff");
        return true;
    }

    @Override
    public String getDescription() {
        return "Do Y staff";
    }
}
