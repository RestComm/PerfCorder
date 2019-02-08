package org.restcomm.perfcorder.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final Map<String, Number> previousValues = new HashMap();

    public JMX2CSVView(int vmid, JMX2CSVDescriptor descriptor) throws Exception {
        super(null);
        if (vmid > 0) {
            LocalVirtualMachine localVirtualMachine = LocalVirtualMachine
                    .getLocalVirtualMachine(vmid);
            proxyClient = ProxyClient.getProxyClient(localVirtualMachine);
            proxyClient.connect();
        } else {
            proxyClient = ProxyClient.getProxyClient(descriptor.getUrl(),
                    descriptor.getUserName(),
                    descriptor.getPassword());
            proxyClient.connect();
        }
        oName = new ObjectName(descriptor.getObjectName());
        mBeanInfo = proxyClient.getMBeanServerConnection().getMBeanInfo(oName);
        this.descriptor = descriptor;

        initMeta();

    }

    public JMX2CSVView(String url, JMX2CSVDescriptor descriptor) throws Exception {
        super(null);
        proxyClient = ProxyClient.getProxyClient(url, System.getenv("PERF_USER"),
                System.getenv("PERF_PSW"));
        proxyClient.connect();
        oName = new ObjectName(descriptor.getObjectName());
        mBeanInfo = proxyClient.getMBeanServerConnection().getMBeanInfo(oName);
        this.descriptor = descriptor;

        initMeta();

    }

    private void initMeta() throws Exception {
        //verify atts exists
        for (JMXAttribute attAux : descriptor.getAttributes()) {
            proxyClient.getMBeanServerConnection().getAttribute(oName, attAux.getName());
        }

        for (JMXOperation attAux : descriptor.getOperations()) {
            boolean found = false;
            for (MBeanOperationInfo inf : mBeanInfo.getOperations()) {
                if (inf.getName().equals(attAux.getName())
                        && inf.getSignature().length == attAux.getArguments().size()
                        && !inf.getReturnType().startsWith("[")) {

                    found = true;

                    Object[] args = new Object[attAux.getArguments().size()];
                    String[] sig = new String[inf.getSignature().length];
                    for (int i = 0; i < attAux.getArguments().size(); i++) {
                        //convert to proper
                        Class targetClass = null;
                        switch (inf.getSignature()[i].getType()) {
                            case "int":
                                targetClass = Integer.class;
                                break;
                            case "long":
                                targetClass = Long.class;
                                break;
                            default:
                                targetClass = this.getClass().getClassLoader().loadClass(inf.getSignature()[i].getType());
                        }
                        Object converted = new ConvertUtilsBean().convert(attAux.getArguments().get(i),
                                targetClass);
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

    private void appendValue(String name, Number newValue, StringBuilder builder, Boolean delta) {
        Number updatedValue = newValue;
        if (!delta) {
            if (previousValues.containsKey(name)) {
                updatedValue = newValue.doubleValue() - previousValues.get(name).doubleValue();
            }
            previousValues.put(name, newValue);
        }
        builder.append(updatedValue);
        builder.append(CSV_SEPARATOR);
    }

    @Override
    public String printView() throws Exception {

        StringBuilder builder = new StringBuilder();
        for (JMXAttribute attAux : descriptor.getAttributes()) {
            Number attValue = (java.lang.Number) proxyClient.getMBeanServerConnection().getAttribute(oName,
                    attAux.getName());
            appendValue(attAux.getName(), attValue, builder, attAux.getDelta());
        }

        for (int i = 0; i < descriptor.getOperations().size(); i++) {
            JMXOperation attAux = descriptor.getOperations().get(i);
            Object[] args = arguments.get(i);
            String[] sig = signatures.get(i);
            Number result = (Number) proxyClient.getMBeanServerConnection().invoke(oName,
                    attAux.getName(),
                    args,
                    sig);
            appendValue(attAux.toString(), result, builder, attAux.getDelta());
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
        for (JMXAttribute attAux : descriptor.getAttributes()) {
            builder.append(attAux.getName());
            builder.append(CSV_SEPARATOR);
        }
        for (JMXOperation attAux : descriptor.getOperations()) {
            builder.append(attAux.toString());
            builder.append(CSV_SEPARATOR);
        }
        return builder.toString();
    }
}
