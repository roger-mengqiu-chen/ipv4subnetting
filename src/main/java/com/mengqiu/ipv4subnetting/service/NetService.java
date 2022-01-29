package com.mengqiu.ipv4subnetting.service;

import com.mengqiu.ipv4subnetting.model.Net;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Service
public class NetService {

    /**
     * convert String to an int[]
     * s is IP address
     * @param s
     * @return arr
     */
    public int[] stringToArr (String s) {
        int [] arr = new int [4];
        String [] sArr = s.split ("\\.");
        for (int i = 0; i < 4; i ++) {
            try {
                int n = Integer.parseInt(sArr[i]);
                if (n > 255) {
                    return null;
                }
                arr[i] = n;
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return arr;
    }

    /**
     * Convert prefix to subnet mask
     * IPv4 address has 4 sections. Each section has 8 bits:
     *      00000000.00000000.00000000.00000000
     * Prefix is the number of bits with value of 1:
     *      prefix = 24, network is:
     *      11111111.11111111.11111111.00000000
     * @param prefix
     * @return
     */
    public int[] prefixToMask (int prefix) {
        if (prefix > 0 && prefix <= 32) {
            int[] mask = new int[4];

            // find out which section should be calculated
            int sectionNum = prefix / 8;
            // find out the left bits
            int bitOfSection = prefix % 8;

            // calculate the decimal representation of a section
            // For example, bitOfSection = 4
            // section = 2^7 + 2^6 + 2^5 + 2^4
            int section = 0;
            for (int i = 1; i <= bitOfSection; i++) {
                section += (int) Math.pow(2, 8 - i);
            }

            for (int i = 0; i < 4; i++) {
                if (i < sectionNum) {
                    mask[i] = 255;
                } else if (i == sectionNum && bitOfSection != 0) {
                    mask[i] = section;
                }
            }
            return mask;
        }
        else {
            return null;
        }
    }

    /**
     * Get prefix from mask
     * @param mask
     * @return
     */
    public int maskToPrefix(int[] mask) {
        String bin = "";
        for (int i = 0; i < 4; i ++) {
            bin += Integer.toBinaryString(mask[i]);
        }
        int p = 0;
        for (int i = 0; i < bin.length(); i++) {
            if (bin.charAt(i) == '1') {
                p ++;
            }
            else {
                break;
            }
        }
        return p;
    }

    /**
     * Convert int[] to String
     * @param arr
     * @return String
     */
    public String arrToString (int[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i ++) {
            sb.append(arr[i]);
            if (i != 3) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    /**
     * Get network address according to IP and mask
     * Use logical AND to calculate each bit of the network
     * @param IP
     * @param mask
     * @return networkAddress
     */
    public int[] getNetwork (int[] IP, int[] mask) {
        int [] networkAddress = new int [4];
        for (int i = 0; i < 4; i ++) {
            networkAddress[i] = IP[i] & mask [i];
        }
        return networkAddress;
    }

    public long getMaxNumberOfHost(Net net) {
        int prefix = net.getPrefix();
        int[] ipAddress = net.getIpAddress();
        return (int) Math.pow(2, 32 - prefix) - 2;
    }

    /**
     * This will convert ip address to a long number (e.g., 192.168.1.32 = 3232235808)
     * @param ip
     * @return
     */
    public long ipToLong (int[] ip) {
        long dec_address = 0;

        for (int i = 0; i < 4; i ++) {
            dec_address += ip[i] * (long)Math.pow(2, (3-i)*8);
        }
        return dec_address;
    }

    /**
     * This converts long number back to ip address int[]
     * @param number
     * @return
     */
    public int[] longToArr (long number) {
        int[] ip = new int[4];

        for (int i = 0; i < 4; i ++) {
            ip[i] = (int) (number / ((long)Math.pow(2, 24 - i * 8)));
            number %= (long)Math.pow(2, 24 - i * 8);
        }
        return ip;
    }

    /**
     * Loop through the map and add entries to an ArrayList
     * Each entry contains name of the subnet and the number of hosts in this subnet
     * The list will be sorted before returned
     * @param map
     * @return
     */
    public ArrayList<Net> getNetListFromMap(Map<String, Integer> map) {
        ArrayList<Net> list = new ArrayList<>();
        map.forEach((name, numOfHosts) -> {
            Net n = new Net(name, numOfHosts);
            n.setMask(prefixToMask(n.getPrefix()));
            list.add(n);
        });
        Collections.sort(list);
        return list;
    }

    /**
     * Assign Ip address to each net
     * The list has been sorted
     * @param nets
     * @param parentAddress
     */
    public int assignIPtoEachNet(ArrayList<Net> nets, int[] parentAddress, int prefix) {

        // Get max number of address of parentAddress:
        long maxNumbOfAddress = (long)Math.pow(2, 32 - prefix);

        for (int i = nets.size() - 1; i >= 0; i --) {
            Net net = nets.get(i);

            // check if there's enough space for a net
            long addressTaken = net.getAllocatedAddresses();
            if (addressTaken > maxNumbOfAddress) {
                return -1;
            }

            net.setIpAddress(parentAddress);
            // deduct available address
            maxNumbOfAddress -= addressTaken;
            // if it is the last subnet, exit the loop
            if (i == 0) {
                break;
            }

            // calculate the ip address for next subnet
            long nextIP = ipToLong(net.getIpAddress()) + net.getAllocatedAddresses();
            parentAddress = longToArr(nextIP);
        }
        return 1;
    }
}
