package compoz;

import org.oz.composite.ChainExcecutionAbs;
import org.oz.composite.IProcessData;
import org.oz.composite.ProcessUtil;

public class DoXLeaf extends ChainExcecutionAbs {
    @Override
    public boolean run(IProcessData pData) {
        pData.addMessage(ProcessUtil.OK, "Successfully do X things");
        return true;
    }

    @Override
    public String getDescription() {
        return "Do x staff";
    }
}
