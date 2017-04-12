package org.restcomm.perfcorder.collector;

import java.util.ArrayList;
import java.util.List;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ObjectName;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.restcomm.perfcorder.collector.jmx.LocalVirtualMachine;
import org.restcomm.perfcorder.collector.jmx.ProxyClient;

public class JMX2CSVView extends AbstractConsoleView {

    private static final String CSV_SEPARATOR = ",";
    private final ProxyClient proxyClient;
    private final JMX2CSVDescriptor descriptor;
    private final ObjectName oName;
    private final MBeanInfo mBeanInfo;
    private final List<String[]> signatures = new ArrayList();
    private final List<Object[]> arguments = new ArrayList();

    public JMX2CSVView(int vmid, JMX2CSVDescriptor descriptor) throws Exception {
        super(null);
        LocalVirtualMachine localVirtualMachine = LocalVirtualMachine
                .getLocalVirtualMachine(vmid);
        proxyClient = ProxyClient.getProxyClient(localVirtualMachine);
        proxyClient.connect();
        oName = new ObjectName(descriptor.getObjectName());
        mBeanInfo = proxyClient.getMBeanServerConnection().getMBeanInfo(oName);
        this.descriptor = descriptor;

        //verify atts exists
        for (String attAux : descriptor.getAttributes()) {
            proxyClient.getMBeanServerConnection().getAttribute(oName, attAux);
        }

        for (JMXOperation attAux : descriptor.getOperations()) {
            boolean found = false;
            for (MBeanOperationInfo inf : mBeanInfo.getOperations()) {
                if (inf.getName().equals(attAux.getName())
                        && inf.getSignature().length == attAux.getArguments().size()) {
                    found = true;

                    Object[] args = new Object[attAux.getArguments().size()];
                    String[] sig = new String[inf.getSignature().length];
                    for (int i = 0; i < attAux.getArguments().size(); i++) {
                        //convert to proper
                        Object converted = new ConvertUtilsBean().convert(attAux.getArguments().get(i), 
                                this.getClass().getClassLoader().loadClass(inf.getSignature()[i].getType()));
                        args[i] = converted;
                        sig[i] = inf.getSignature()[i].getType();
                    }
                    arguments.add(args);
                    signatures.add(sig);
                }
            }
            if (!found) {
                throw new RuntimeException("operation not found:" + attAux);
            }
        }
    }

    @Override
    public String printView() throws Exception {

        StringBuilder builder = new StringBuilder();
        for (String attAux : descriptor.getAttributes()) {
            Object obj = proxyClient.getMBeanServerConnection().getAttribute(oName, attAux);
            builder.append(obj);
            builder.append(CSV_SEPARATOR);
        }

        for (int i = 0; i < descriptor.getOperations().size(); i++) {
            JMXOperation attAux = descriptor.getOperations().get(i);
            Object[] args = arguments.get(i);
            String[] sig = signatures.get(i);
            Object result = proxyClient.getMBeanServerConnection().invoke(oName,
                    attAux.getName(),
                    args,
                    sig);
            builder.append(result);
            builder.append(CSV_SEPARATOR);
        }

        return builder.toString();
    }

    @Override
    public boolean isTopBarRequired() {
        return false;
    }

    @Override
    public boolean isClearingRequired() {
        return false;
    }

    @Override
    public String printHeader() throws Exception {
        StringBuilder builder = new StringBuilder();
        for (String attAux : descriptor.getAttributes()) {
            builder.append(attAux);
            builder.append(CSV_SEPARATOR);
        }
        for (JMXOperation attAux : descriptor.getOperations()) {
            builder.append(attAux.getName());
            builder.append(CSV_SEPARATOR);
        }
        return builder.toString();
    }
}
