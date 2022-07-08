package compoz;

import org.oz.composite.ChainExcecutionAbs;
import org.oz.composite.IProcessData;
import org.springframework.stereotype.Component;

public class Compo extends ChainExcecutionAbs {

    @Override
    public boolean run(IProcessData pData) {
        return true;
    }

    @Override
    public String getDescription() {
        return "run children";
    }
}
