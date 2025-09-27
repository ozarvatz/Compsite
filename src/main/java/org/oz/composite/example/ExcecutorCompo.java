package org.oz.composite.example;

import org.oz.composite.ChainExcecutionAbs;
import org.oz.composite.IProcessData;
import org.oz.composite.ProcessUtil;
import org.springframework.stereotype.Component;

//import javax.annotation.PostConstruct;

@Component("ExcecutorCompo")
public class ExcecutorCompo extends ChainExcecutionAbs {
    @Override
    public String getDescription() {
        return "";
    }


    @Override
    public boolean run(IProcessData pData) {
        pData.addMessage(ProcessUtil.OK, "Test excecutorCompo runner.");
        return true;
    }
}
