package org.oz.composite.example;

import org.oz.composite.ChainExcecutionAbs;
import org.oz.composite.IProcessData;
import org.oz.composite.ProcessUtil;
import org.springframework.stereotype.Component;

@Component("DoYLeaf")
public class DoYLeaf extends ChainExcecutionAbs {
    @Override
    public boolean run(IProcessData pData) {
        pData.addMessage(ProcessUtil.OK, String.format("Do Y staff %s", "lama pfff"));
        return true;
    }

    @Override
    public String getDescription() {
        return "Do Y staff";
    }
}
