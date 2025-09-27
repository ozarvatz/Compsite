package org.oz.composite.example;

import org.oz.composite.ChainExcecutionAbs;
import org.oz.composite.IProcessData;
import org.oz.composite.ProcessUtil;
import org.springframework.stereotype.Component;

@Component("DoXLeaf")
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
