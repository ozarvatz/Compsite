package compoz;

import org.oz.composite.ChainExcecutionAbs;
import org.oz.composite.IProcessData;
import org.oz.composite.ProcessUtil;
import org.springframework.stereotype.Component;

public class DoXLeaf extends ChainExcecutionAbs {
    @Override
    public boolean run(IProcessData pData) {
        try { Thread.sleep(300); } catch (Exception e) { e.printStackTrace(); }
        pData.addMessage(ProcessUtil.OK, "Successfully do X things");
        return true;
    }

    @Override
    public String getDescription() {
        return "Do x staff";
    }
}
