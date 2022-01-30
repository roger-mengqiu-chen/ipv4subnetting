package com.mengqiu.ipv4subnetting.controller;

import com.mengqiu.ipv4subnetting.model.Net;
import com.mengqiu.ipv4subnetting.request.NetsRequest;
import com.mengqiu.ipv4subnetting.response.JsonResponse;
import com.mengqiu.ipv4subnetting.service.NetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/subnet")
public class FormController {

    @Autowired
    private NetService netService;

    @PostMapping
    public JsonResponse subnet (@RequestBody NetsRequest request) {
        String hostIpString = request.getHostIp();
        String maskString = request.getMask();
        Map<String, Integer> subnets = request.getSubnets();

        // parse hostIP
        int[] hostIp = netService.stringToArr(hostIpString);
        if (hostIp == null) {
            return new JsonResponse("Fail", "Invalid host IP address");
        }

        // get mask address of parent network, also need validation for prefix or mask
        int[] mask;
        int prefix = -1;
        if (maskString.length() > 2) {
            mask = netService.stringToArr(maskString);
            prefix = netService.maskToPrefix(mask);
            if (mask == null) {
                return new JsonResponse("Fail", "Invalid mask address");
            }
        }
        else {
            try {
                prefix = Integer.parseInt(maskString);
                mask = netService.prefixToMask(prefix);
                if (mask == null) {
                    return new JsonResponse("Fail","Invalid prefix");
                }
            } catch (NumberFormatException e) {
                return new JsonResponse("Fail", "Invalid prefix");
            }
        }

        // get IP address of parent network
        int[] parentAddress = netService.getNetwork(hostIp, mask);

        // generate a list of Net from the user request
        ArrayList<Net> nets = netService.getNetListFromMap(subnets);

        // assign IP address to the net in the list
        if (netService.assignIPtoEachNet(nets, parentAddress, prefix) > 0) {
            return new JsonResponse("Success", nets);
        }
        else {
            return new JsonResponse("Fail", "Not enough address !");
        }
    }



}
