package compoz;

import org.oz.composite.ChainExcecutionAbs;
import org.oz.composite.IProcessData;
import org.oz.composite.ProcessUtil;

public class FailedDoZLeaf extends ChainExcecutionAbs {
    @Override
    public String getDescription() {
        return "failed to do Z";
    }

    @Override
    public boolean run(IProcessData pData) {
        pData.addMessage(ProcessUtil.PROCESS_FAIL, "failed to do the Z things, Z is ded, baby");
        return false;
    }
}
