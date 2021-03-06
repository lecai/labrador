package com.threathunter.labrador.core.transform;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.core.env.Env;

import java.util.List;

/**
 * 
 */
public class EnvExtract extends Extract {
    @Override
    public List<Variable> getVariablesBySouceName(String sourceName) {
        return Env.getVariables().getVariablesByScene(sourceName);
    }
}
