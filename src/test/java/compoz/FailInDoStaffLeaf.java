package compoz;

import org.oz.composite.ChainExcecutionAbs;
import org.oz.composite.IProcessData;
import org.oz.composite.ProcessUtil;

public class FailInDoStaffLeaf extends ChainExcecutionAbs {
    @Override
    public boolean run(IProcessData pData) {
        pData.addMessage(ProcessUtil.PROCESS_FAIL, "fail in do staff");
        return false;
    }

    @Override
    public String getDescription() {
        return "Fail in do staff";
    }
}
