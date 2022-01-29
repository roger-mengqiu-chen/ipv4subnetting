# IPv4 subnetting auto-configuration
This is a small java RESTapi used to configure IPv4 subnetting. <br>
You can send POST request to `/subset` then get JSON response from it 
Here is an example of request:
```json
{
    "hostIp":"210.100.56.0",
    "mask":"255.255.255.0",
    "subnets":{
        "net1":200,
        "net2":4
    }
}
```
Within the request, "hostIp" is the IP address of parent, "subnets" contains the entries about each subnet's name and required number of hosts.
Here is an example of response:
```json
[
    {
        "name": "net2",
        "ipAddress": [
            210,
            100,
            56,
            32
        ],
        "numberOfHosts": 4,
        "prefix": 29,
        "mask": [
            255,
            255,
            255,
            248
        ],
        "allocatedAddresses": 8
    },
    {
        "name": "net1",
        "ipAddress": [
            210,
            100,
            56,
            0
        ],
        "numberOfHosts": 21,
        "prefix": 27,
        "mask": [
            255,
            255,
            255,
            224
        ],
        "allocatedAddresses": 32
    }
]
```
## Theory used in this program

### **IPv4 network**
IPv4 is 32 bit long IP address. We can divide the address into 4 sections, each of which is 8 bit long. In human-readable format, we separate these sections with "." (e.g., 192.168.1.1).<br>
All of the IPv4 address can be converted to binary format: <br>
For example, 192.168.1.1  can be written in 11000000.10101000.00000001.00000001<br>
| Decimal | Binary |
| --- | --- |
| 192 | 1100 0000 |
| 168 | 1010 0000 |
| 1 | 0000 0001 |

<br>


### **Prefix and subnet mask**
Prefix is the number of "1"s in IPv4 binary format address from left to right. It is used to describe which part is the network address (The part with all "1"s is the network portion). <br>
For example, 192.168.1.1 /24 <br>
Prefix /24 can be written in binary format: <br>
11111111 11111111 11111111 00000000 <br>
The first 24 bits are all "1"s. <br>
To convert it to human-readable format, we use subnet mask:
255.255.255.0 <br>
| Decimal | Binary |
| --- | --- |
| 255 | 11111111 |

<br>

### **Determine the network**
IPv4 address has two portions: network portion and host portion. To determine the network portion, use logical AND for each bit: <br>
For example: 192.168.1.1 /24 

| Field | Value |
| --- | --- |
| IPv4 host | 11000000 10101000 00000001 00000001 |
| Subnet Mask | 11111111 11111111 11111111 00000000 |
| Logical AND | 11000000 10101000 00000001 00000000 |

The logical AND result is network address: 192.168.1.0 <br>
And the host portion is last ".1". (i.e., the last 8 bits of IPv4 binary address)
<br>


### **Assign the number of IP addresses according to the number of hosts**

Each network has 2 reserved addresses: broadcast address and network address.
<br>
Broadcast address is the address with all "1"s in host portion (e.g., 192.168.1.0 /24 broadcast address is 192.168.1.255). 
<br>
Network address is the address with all "0"s in host portion (e.g., 192.168.1.128 /25 network address is 192.168.1.128) 
<br>
<br>
Each bit of the network address can hold 2 host addresses (0 and 1). When we allocate IP address for hosts, we caculate the required bits from right to left. 
<br>
For example, if we want to take 6 addresses from 192.168.1.0 /24 network:
<br>
* Convert IP address to binary form: 11000000 10101000 00000001 00000000
* 6 ≈ 2^3, so 3 bits will be used
* The rightmost 3 bits are the host part. The rest is network part
* The prefix is 32 - 3 = 29. The mask is 11111111 11111111 11111111 11111000, which is 255.255.255.248 <br>
<br>

When we allocate addresses to hosts, remember to add broadcast and network address to the required number of hosts (i.e., 4 required hosts need at least 6 addresses).

<br>
<br>

### **Divide the parent network according to the required number of host addresses**
My strategy is sorting the subnets according to their size. Then allocate addresses from the biggest one.
<br>
For example, network address is 192.168.1.0 /24 and the table below is the required hosts for each network<br>
| network name | hosts |
| --- | --- |
| network1 | 2 |
| network2 | 20 |
| network3 | 6 |
<br>
Then we can allocate addresses as below: 

| network name | allocated addresses | prefix |
| --- | --- | --- |
| network2 | 32 | 27 |
| network3 | 8 | 29 |
| network1 | 4 | 30 |

<br>
Finally, allocate network addresses from the biggest one 

| network name | network address |
| --- | --- |
| network2 | 192.168.1.0 |
| network3 | 192.168.1.32 |
| network1 | 192.168.1.40 |



