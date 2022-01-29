package com.mengqiu.ipv4subnetting.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Net implements Comparable<Net> {

    private String name;

    private int[] ipAddress;

    private long numberOfHosts;

    private int prefix;

    private int[] mask;

    private long allocatedAddresses;

    public Net (String name, int numberOfHosts) {
        this.name = name;
        this.numberOfHosts = numberOfHosts;
        this.prefix = calculatePrefix(numberOfHosts);
    }

    /**
     * This commpare two subnets according to the number of their hosts
     * @param net
     * @return
     */
    @Override
    public int compareTo(Net net) {
            if (this.numberOfHosts - net.getNumberOfHosts() > 0) {
                return 1;
            }
            else if (this.numberOfHosts - net.getNumberOfHosts() < 0) {
                return -1;
            }
            else {
                return 0;
            }
    }

    /**
     * Calculate required addresses for the hosts
     * Also return prefix for this network
     * @param numberOfHosts
     * @return allocatedAddresses
     */
    public int calculatePrefix (int numberOfHosts) {
        int i;
        for (i = 0; i < 32 && this.allocatedAddresses == 0; i ++) {
            int addresses = (int)Math.pow(2, i);
            // found the suitable number of addresses
            if (numberOfHosts + 2 <= addresses) {
                this.allocatedAddresses = addresses;
            }
        }
        return 32 - i + 1;
    }
}
