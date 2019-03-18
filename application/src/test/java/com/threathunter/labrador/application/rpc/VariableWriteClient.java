package com.threathunter.labrador.application.rpc;

import com.threathunter.labrador.application.rpc.service.VariableQueryService;
import com.threathunter.labrador.application.rpc.service.VariableWriteService;
import com.threathunter.labrador.rpc.client.RpcReference;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 
 */
@Component
public class VariableWriteClient {

    @RpcReference
    private VariableWriteService variableWriteService;

    public void writeVariable(String name, String variable, Map<String, Object> kv) throws Exception {
        this.variableWriteService.writeVariable(name, variable, kv);
    }

}
