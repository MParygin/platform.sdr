package platform.sdr.util;

/**
 *
 */
public class FrequencyInfo {

    static FrequencyInfo frequencyInfo[] = {
        new FrequencyInfo(60000L, 60000L, "MSF Time Signal"),
        new FrequencyInfo(75000L, 75000L, "HGB Time Signal"),
        new FrequencyInfo(77500L, 77500L, "DCF77 Time Signal"),
        new FrequencyInfo(153000L, 279000L, "AM - Long Wave"),
        new FrequencyInfo(530000L, 1710000L, "Broadcast AM Med Wave"),
        new FrequencyInfo(1800000L, 1809999L, "160M CW/Digital Modes"),
        new FrequencyInfo(1810000L, 1810000L, "160M CW QRP"),
        new FrequencyInfo(1810001L, 1842999L, "160M CW"),
        new FrequencyInfo(1843000L, 1909999L, "160M SSB/SSTV/Wide Band"),
        new FrequencyInfo(1910000L, 1910000L, "160M SSB QRP"),
        new FrequencyInfo(1910001L, 1994999L, "160M SSB/SSTV/Wide Band"),
        new FrequencyInfo(1995000L, 1999999L, "160M Experimental"),
        new FrequencyInfo(2300000L, 2495000L, "120M Short Wave"),
        new FrequencyInfo(3200000L, 3400000L, "90M Short Wave"),
        new FrequencyInfo(3500000L, 3524999L, "80M Extra CW"),
        new FrequencyInfo(3525000L, 3579999L, "80M CW"),
        new FrequencyInfo(3580000L, 3589999L, "80M RTTY"),
        new FrequencyInfo(3590000L, 3590000L, "80M RTTY DX"),
        new FrequencyInfo(3590001L, 3599999L, "80M RTTY"),
        new FrequencyInfo(3600000L, 3699999L, "75M Extra SSB"),
        new FrequencyInfo(3700000L, 3789999L, "75M Ext/Adv SSB"),
        new FrequencyInfo(3790000L, 3799999L, "75M Ext/Adv DX Window"),
        new FrequencyInfo(3800000L, 3844999L, "75M SSB"),
        new FrequencyInfo(3845000L, 3845000L, "75M SSTV"),
        new FrequencyInfo(3845001L, 3884999L, "75M SSB"),
        new FrequencyInfo(3885000L, 3885000L, "75M AM CaLing Frequency"),
        new FrequencyInfo(3885001L, 3999999L, "75M SSB"),
        new FrequencyInfo(4750000L, 4999999L, "60M Short Wave"),
        new FrequencyInfo(5330500L, 5330500L, "60M Channel 1"),
        new FrequencyInfo(5346500L, 5346500L, "60M Channel 2"),
        new FrequencyInfo(5366500L, 5366500L, "60M Channel 3"),
        new FrequencyInfo(5371500L, 5371500L, "60M Channel 4"),
        new FrequencyInfo(5403500L, 5403500L, "60M Channel 5"),
        new FrequencyInfo(5900000L, 6200000L, "49M Short Wave"),
        new FrequencyInfo(7000000L, 7024999L, "40M Extra CW"),
        new FrequencyInfo(7025000L, 7039999L, "40M CW"),
        new FrequencyInfo(7040000L, 7040000L, "40M RTTY DX"),
        new FrequencyInfo(7040001L, 7099999L, "40M RTTY"),
        new FrequencyInfo(7100000L, 7124999L, "40M CW"),
        new FrequencyInfo(7125000L, 7170999L, "40M Ext/Adv SSB"),
        new FrequencyInfo(7171000L, 7171000L, "40M SSTV"),
        new FrequencyInfo(7171001L, 7174999L, "40M Ext/Adv SSB"),
        new FrequencyInfo(7175000L, 7289999L, "40M SSB"),
        new FrequencyInfo(7290000L, 7290000L, "40M AM CaLing Frequency"),
        new FrequencyInfo(7290001L, 7299999L, "40M SSB"),
        new FrequencyInfo(7300000L, 7350000L, "41M Short Wave"),
        new FrequencyInfo(9400000L, 9900000L, "31M Short Wave"),
        new FrequencyInfo(10100000L, 10129999L, "30M CW"),
        new FrequencyInfo(10130000L, 10139999L, "30M RTTY"),
        new FrequencyInfo(10140000L, 10149999L, "30M Packet"),
        new FrequencyInfo(11600000L, 12100000L, "25M Short Wave"),
        new FrequencyInfo(13570000L, 13870000L, "22M Short Wave"),
        new FrequencyInfo(14000000L, 14024999L, "20M Extra CW"),
        new FrequencyInfo(14025000L, 14069999L, "20M CW"),
        new FrequencyInfo(14070000L, 14094999L, "20M RTTY"),
        new FrequencyInfo(14095000L, 14099499L, "20M Packet"),
        new FrequencyInfo(14099500L, 14099999L, "20M CW"),
        new FrequencyInfo(14100000L, 14100000L, "20M NCDXF Beacons"),
        new FrequencyInfo(14100001L, 14100499L, "20M CW"),
        new FrequencyInfo(14100500L, 14111999L, "20M Packet"),
        new FrequencyInfo(14112000L, 14149999L, "20M CW"),
        new FrequencyInfo(14150000L, 14174999L, "20M Extra SSB"),
        new FrequencyInfo(14175000L, 14224999L, "20M Ext/Adv SSB"),
        new FrequencyInfo(14225000L, 14229999L, "20M SSB"),
//        new FrequencyInfo(14230000L, 14230000L, "20M SSTV"),
        new FrequencyInfo(14230000L, 14285999L, "20M SSB"),
        new FrequencyInfo(14286000L, 14286000L, "20M AM CaLing Frequency"),
        new FrequencyInfo(14286001L, 14349999L, "20M SSB"),
        new FrequencyInfo(15100000L, 15800000L, "19M Short Wave"),
        new FrequencyInfo(17480000L, 17900000L, "16M Short Wave"),
        new FrequencyInfo(18068000L, 18099999L, "17M CW"),
        new FrequencyInfo(18100000L, 18104999L, "17M RTTY"),
        new FrequencyInfo(18105000L, 18109999L, "17M Packet"),
        new FrequencyInfo(18110000L, 18110000L, "17M NCDXF Beacons"),
        new FrequencyInfo(18110001L, 18167999L, "17M SSB"),
        new FrequencyInfo(18900000L, 19020000L, "15M Short Wave"),
        new FrequencyInfo(21000000L, 21024999L, "15M Extra CW"),
        new FrequencyInfo(21025000L, 21069999L, "15M CW"),
        new FrequencyInfo(21070000L, 21099999L, "15M RTTY"),
        new FrequencyInfo(21100000L, 21109999L, "15M Packet"),
        new FrequencyInfo(21110000L, 21149999L, "15M CW"),
        new FrequencyInfo(21150000L, 21150000L, "15M NCDXF Beacons"),
        new FrequencyInfo(21150001L, 21199999L, "15M CW"),
        new FrequencyInfo(21200000L, 21224999L, "15M Extra SSB"),
        new FrequencyInfo(21225000L, 21274999L, "15M Ext/Adv SSB"),
        new FrequencyInfo(21275000L, 21339999L, "15M SSB"),
        new FrequencyInfo(21340000L, 21340000L, "15M SSTV"),
        new FrequencyInfo(21340001L, 21449999L, "15M SSB"),
        new FrequencyInfo(21450000L, 21850000L, "13M Short Wave"),
        new FrequencyInfo(24890000L, 24919999L, "12M CW"),
        new FrequencyInfo(24920000L, 24924999L, "12M RTTY"),
        new FrequencyInfo(24925000L, 24929999L, "12M Packet"),
        new FrequencyInfo(24930000L, 24930000L, "12M NCDXF Beacons"),
        new FrequencyInfo(24930001L, 24989999L, "12M SSB Wideband"),
        new FrequencyInfo(25600000L, 26100000L, "11M Short Wave"),
        new FrequencyInfo(28000000L, 28069999L, "10M CW"),
        new FrequencyInfo(28070000L, 28149999L, "10M RTTY"),
        new FrequencyInfo(28150000L, 28199999L, "10M CW"),
        new FrequencyInfo(28200000L, 28200000L, "10M NCDXF Beacons"),
        new FrequencyInfo(28200001L, 28299999L, "10M Beacons"),
        new FrequencyInfo(28300000L, 28679999L, "10M SSB"),
        new FrequencyInfo(28680000L, 28680000L, "10M SSTV"),
        new FrequencyInfo(28680001L, 28999999L, "10M SSB"),
        new FrequencyInfo(29000000L, 29199999L, "10M AM"),
        new FrequencyInfo(29200000L, 29299999L, "10M SSB"),
        new FrequencyInfo(29300000L, 29509999L, "10M SateLite Downlinks"),
        new FrequencyInfo(29510000L, 29519999L, "10M Deadband"),
        new FrequencyInfo(29520000L, 29589999L, "10M Repeater Inputs"),
        new FrequencyInfo(29590000L, 29599999L, "10M Deadband"),
        new FrequencyInfo(29600000L, 29600000L, "10M FM Simplex"),
        new FrequencyInfo(29600001L, 29609999L, "10M Deadband"),
        new FrequencyInfo(29610000L, 29699999L, "10M Repeater Outputs"),
        new FrequencyInfo(50000000L, 50059999L, "6M CW"),
        new FrequencyInfo(50060000L, 50079999L, "6M Beacon Sub-Band"),
        new FrequencyInfo(50080000L, 50099999L, "6M CW"),
        new FrequencyInfo(50100000L, 50124999L, "6M DX Window"),
        new FrequencyInfo(50125000L, 50125000L, "6M CaLing Frequency"),
        new FrequencyInfo(50125001L, 50299999L, "6M SSB"),
        new FrequencyInfo(50300000L, 50599999L, "6M AL Modes"),
        new FrequencyInfo(50600000L, 50619999L, "6M Non Voice"),
        new FrequencyInfo(50620000L, 50620000L, "6M Digital Packet CaLing"),
        new FrequencyInfo(50620001L, 50799999L, "6M Non Voice"),
        new FrequencyInfo(50800000L, 50999999L, "6M RC"),
        new FrequencyInfo(51000000L, 51099999L, "6M Pacific DX Window"),
        new FrequencyInfo(51100000L, 51119999L, "6M Deadband"),
        new FrequencyInfo(51120000L, 51179999L, "6M Digital Repeater Inputs"),
        new FrequencyInfo(51180000L, 51479999L, "6M Repeater Inputs"),
        new FrequencyInfo(51480000L, 51619999L, "6M Deadband"),
        new FrequencyInfo(51620000L, 51679999L, "6M Digital Repeater Outputs"),
        new FrequencyInfo(51680000L, 51979999L, "6M Repeater Outputs"),
        new FrequencyInfo(51980000L, 51999999L, "6M Deadband"),
        new FrequencyInfo(52000000L, 52019999L, "6M Repeater Inputs"),
        new FrequencyInfo(52020000L, 52020000L, "6M FM Simplex"),
        new FrequencyInfo(52020001L, 52039999L, "6M Repeater Inputs"),
        new FrequencyInfo(52040000L, 52040000L, "6M FM Simplex"),
        new FrequencyInfo(52040001L, 52479999L, "6M Repeater Inputs"),
        new FrequencyInfo(52480000L, 52499999L, "6M Deadband"),
        new FrequencyInfo(52500000L, 52524999L, "6M Repeater Outputs"),
        new FrequencyInfo(52525000L, 52525000L, "6M Primary FM Simplex"),
        new FrequencyInfo(52525001L, 52539999L, "6M Deadband"),
        new FrequencyInfo(52540000L, 52540000L, "6M Secondary FM Simplex"),
        new FrequencyInfo(52540001L, 52979999L, "6M Repeater Outputs"),
        new FrequencyInfo(52980000L, 52999999L, "6M Deadbands"),
        new FrequencyInfo(53000000L, 53000000L, "6M Remote Base FM Spx"),
        new FrequencyInfo(53000001L, 53019999L, "6M Repeater Inputs"),
        new FrequencyInfo(53020000L, 53020000L, "6M FM Simplex"),
        new FrequencyInfo(53020001L, 53479999L, "6M Repeater Inputs"),
        new FrequencyInfo(53480000L, 53499999L, "6M Deadband"),
        new FrequencyInfo(53500000L, 53519999L, "6M Repeater Outputs"),
        new FrequencyInfo(53520000L, 53520000L, "6M FM Simplex"),
        new FrequencyInfo(53520001L, 53899999L, "6M Repeater Outputs"),
        new FrequencyInfo(53900000L, 53900000L, "6M FM Simplex"),
        new FrequencyInfo(53900010, 53979999L, "6M Repeater Outputs"),
        new FrequencyInfo(53980000L, 53999999L, "6M Deadband"),
        new FrequencyInfo(144000000L, 144099999L, "2M CW"),
        new FrequencyInfo(144100000L, 144199999L, "2M CW/SSB"),
        new FrequencyInfo(144200000L, 144200000L, "2M CaLing"),
        new FrequencyInfo(144200001L, 144274999L, "2M CW/SSB"),
        new FrequencyInfo(144275000L, 144299999L, "2M Beacon Sub-Band"),
        new FrequencyInfo(144300000L, 144499999L, "2M SateLite"),
        new FrequencyInfo(144500000L, 144599999L, "2M Linear Translator Inputs"),
        new FrequencyInfo(144600000L, 144899999L, "2M FM Repeater"),
        new FrequencyInfo(144900000L, 145199999L, "2M FM Simplex"),
        new FrequencyInfo(145200000L, 145499999L, "2M FM Repeater"),
        new FrequencyInfo(145500000L, 145799999L, "2M FM Simplex"),
        new FrequencyInfo(145800000L, 145999999L, "2M SateLite"),
        new FrequencyInfo(146000000L, 146399999L, "2M FM Repeater"),
        new FrequencyInfo(146400000L, 146609999L, "2M FM Simplex"),
        new FrequencyInfo(146610000L, 147389999L, "2M FM Repeater"),
        new FrequencyInfo(147390000L, 147599999L, "2M FM Simplex"),
        new FrequencyInfo(147600000L, 147999999L, "2M FM Repeater"),
        new FrequencyInfo(222000000L, 222024999L, "125M EME/Weak Signal"),
        new FrequencyInfo(222025000L, 222049999L, "125M Weak Signal"),
        new FrequencyInfo(222050000L, 222059999L, "125M Propagation Beacons"),
        new FrequencyInfo(222060000L, 222099999L, "125M Weak Signal"),
        new FrequencyInfo(222100000L, 222100000L, "125M SSB/CW CaLing"),
        new FrequencyInfo(222100001L, 222149999L, "125M Weak Signal CW/SSB"),
        new FrequencyInfo(222150000L, 222249999L, "125M Local Option"),
//        new FrequencyInfo(222250000L, 223380000L, "125M FM Repeater Inputs"),
        new FrequencyInfo(222380001L, 223399999L, "125M General"),
        new FrequencyInfo(223400000L, 223519999L, "125M FM Simplex"),
        new FrequencyInfo(223520000L, 223639999L, "125M Digital/Packet"),
        new FrequencyInfo(223640000L, 223700000L, "125M Links/Control"),
        new FrequencyInfo(223700001L, 223709999L, "125M General"),
        new FrequencyInfo(223710000L, 223849999L, "125M Local Option"),
        new FrequencyInfo(223850000L, 224980000L, "125M Repeater Outputs"),
        new FrequencyInfo(420000000L, 425999999L, "70CM ATV Repeater"),
        new FrequencyInfo(426000000L, 431999999L, "70CM ATV Simplex"),
        new FrequencyInfo(432000000L, 432069999L, "70CM EME"),
        new FrequencyInfo(432070000L, 432099999L, "70CM Weak Signal CW"),
        new FrequencyInfo(432100000L, 432100000L, "70CM CaLing Frequency"),
        new FrequencyInfo(432100001L, 432299999L, "70CM Mixed Mode Weak Signal"),
        new FrequencyInfo(432300000L, 432399999L, "70CM Propagation Beacons"),
        new FrequencyInfo(432400000L, 432999999L, "70CM Mixed Mode Weak Signal"),
        new FrequencyInfo(433000000L, 434999999L, "70CM AuxiLary/Repeater Links"),
        new FrequencyInfo(435000000L, 437999999L, "70CM SateLite Only"),
        new FrequencyInfo(438000000L, 441999999L, "70CM ATV Repeater"),
        new FrequencyInfo(442000000L, 444999999L, "70CM Local Repeaters"),
        new FrequencyInfo(445000000L, 445999999L, "70CM Local Option"),
        new FrequencyInfo(446000000L, 446000000L, "70CM Simplex"),
        new FrequencyInfo(446000001L, 446999999L, "70CM Local Option"),
        new FrequencyInfo(447000000L, 450000000L, "70CM Local Repeaters"),
        new FrequencyInfo(902000000L, 902099999L, "33CM Weak Signal SSTV/FAX/ACSSB"),
        new FrequencyInfo(902100000L, 902100000L, "33CM Weak Signal CaLing"),
        new FrequencyInfo(902100001L, 902799999L, "33CM Weak Signal SSTV/FAX/ACSSB"),
        new FrequencyInfo(902800000L, 902999999L, "33CM Weak Signal EME/CW"),
        new FrequencyInfo(903000000L, 903099999L, "33CM Digital Modes"),
        new FrequencyInfo(903100000L, 903100000L, "33CM Alternate CaLing"),
        new FrequencyInfo(903100001L, 905999999L, "33CM Digital Modes"),
        new FrequencyInfo(906000000L, 908999999L, "33CM FM Repeater Inputs"),
        new FrequencyInfo(909000000L, 914999999L, "33CM ATV"),
        new FrequencyInfo(915000000L, 917999999L, "33CM Digital Modes"),
        new FrequencyInfo(918000000L, 920999999L, "33CM FM Repeater Outputs"),
        new FrequencyInfo(921000000L, 926999999L, "33CM ATV"),
        new FrequencyInfo(927000000L, 928000000L, "33CM FM Simplex/Links"),
        new FrequencyInfo(1240000000L, 1245999999L, "23CM ATV #1"),
        new FrequencyInfo(1246000000L, 1251999999L, "23CM FMN Point/Links"),
        new FrequencyInfo(1252000000L, 1257999999L, "23CM ATV #2, Digital Modes"),
        new FrequencyInfo(1258000000L, 1259999999L, "23CM FMN Point/Links"),
        new FrequencyInfo(1260000000L, 1269999999L, "23CM Sat Uplinks/Wideband Exp"),
        new FrequencyInfo(1270000000L, 1275999999L, "23CM Repeater Inputs"),
        new FrequencyInfo(1276000000L, 1281999999L, "23CM ATV #3"),
        new FrequencyInfo(1282000000L, 1287999999L, "23CM Repeater Outputs"),
        new FrequencyInfo(1288000000L, 1293999999L, "23CM Simplex ATV/Wideband Exp"),
        new FrequencyInfo(1294000000L, 1294499999L, "23CM Simplex FMN"),
        new FrequencyInfo(1294500000L, 1294500000L, "23CM FM Simplex CaLing"),
        new FrequencyInfo(1294500001L, 1294999999L, "23CM Simplex FMN"),
        new FrequencyInfo(1295000000L, 1295799999L, "23CM SSTV/FAX/ACSSB/Exp"),
        new FrequencyInfo(1295800000L, 1295999999L, "23CM EME/CW Expansion"),
        new FrequencyInfo(1296000000L, 1296049999L, "23CM EME Exclusive"),
        new FrequencyInfo(1296050000L, 1296069999L, "23CM Weak Signal"),
        new FrequencyInfo(1296070000L, 1296079999L, "23CM CW Beacons"),
        new FrequencyInfo(1296080000L, 1296099999L, "23CM Weak Signal"),
        new FrequencyInfo(1296100000L, 1296100000L, "23CM CW/SSB CaLing"),
        new FrequencyInfo(1296100001L, 1296399999L, "23CM Weak Signal"),
        new FrequencyInfo(1296400000L, 1296599999L, "23CM X-Band Translator Input"),
        new FrequencyInfo(1296600000L, 1296799999L, "23CM X-Band Translator Output"),
        new FrequencyInfo(1296800000L, 1296999999L, "23CM Experimental Beacons"),
        new FrequencyInfo(1297000000L, 1300000000L, "23CM Digital Modes"),
        new FrequencyInfo(2300000000L, 2302999999L, "23GHz High Data Rate"),
        new FrequencyInfo(2303000000L, 2303499999L, "23GHz Packet"),
        new FrequencyInfo(2303500000L, 2303800000L, "23GHz TTY Packet"),
        new FrequencyInfo(2303800001L, 2303899999L, "23GHz General"),
        new FrequencyInfo(2303900000L, 2303900000L, "23GHz Packet/TTY/CW/EME"),
        new FrequencyInfo(2303900001L, 2304099999L, "23GHz CW/EME"),
        new FrequencyInfo(2304100000L, 2304100000L, "23GHz CaLing Frequency"),
        new FrequencyInfo(2304100001L, 2304199999L, "23GHz CW/EME/SSB"),
        new FrequencyInfo(2304200000L, 2304299999L, "23GHz SSB/SSTV/FAX/Packet AM/Amtor"),
        new FrequencyInfo(2304300000L, 2304319999L, "23GHz Propagation Beacon Network"),
        new FrequencyInfo(2304320000L, 2304399999L, "23GHz General Propagation Beacons"),
        new FrequencyInfo(2304400000L, 2304499999L, "23GHz SSB/SSTV/ACSSB/FAX/Packet AM"),
        new FrequencyInfo(2304500000L, 2304699999L, "23GHz X-Band Translator Input"),
        new FrequencyInfo(2304700000L, 2304899999L, "23GHz X-Band Translator Output"),
        new FrequencyInfo(2304900000L, 2304999999L, "23GHz Experimental Beacons"),
        new FrequencyInfo(2305000000L, 2305199999L, "23GHz FM Simplex"),
        new FrequencyInfo(2305200000L, 2305200000L, "23GHz FM Simplex CaLing"),
        new FrequencyInfo(2305200001L, 2305999999L, "23GHz FM Simplex"),
        new FrequencyInfo(2306000000L, 2308999999L, "23GHz FM Repeaters"),
        new FrequencyInfo(2309000000L, 2310000000L, "23GHz Control/Aux Links"),
        new FrequencyInfo(2390000000L, 2395999999L, "23GHz Fast-Scan TV"),
        new FrequencyInfo(2396000000L, 2398999999L, "23GHz High Rate Data"),
        new FrequencyInfo(2399000000L, 2399499999L, "23GHz Packet"),
        new FrequencyInfo(2399500000L, 2399999999L, "23GHz Control/Aux Links"),
        new FrequencyInfo(2400000000L, 2402999999L, "24GHz SateLite"),
        new FrequencyInfo(2403000000L, 2407999999L, "24GHz SateLite High-Rate Data"),
        new FrequencyInfo(2408000000L, 2409999999L, "24GHz SateLite"),
        new FrequencyInfo(2410000000L, 2412999999L, "24GHz FM Repeaters"),
        new FrequencyInfo(2413000000L, 2417999999L, "24GHz High-Rate Data"),
        new FrequencyInfo(2418000000L, 2429999999L, "24GHz Fast-Scan TV"),
        new FrequencyInfo(2430000000L, 2432999999L, "24GHz SateLite"),
        new FrequencyInfo(2433000000L, 2437999999L, "24GHz Sat High-Rate Data"),
        new FrequencyInfo(2438000000L, 2450000000L, "24GHz Wideband FM/FSTV/FMTV"),
        new FrequencyInfo(3456000000L, 3456099999L, "3.4GHz General"),
        new FrequencyInfo(3456100000L, 3456100000L, "3.4GHz CaLing Frequency"),
        new FrequencyInfo(3456100001L, 3456299999L, "3.4GHz General"),
        new FrequencyInfo(3456300000L, 3456400000L, "3.4GHz Propagation Beacons"),
        new FrequencyInfo(5760000000L, 5760099999L, "5.7GHz General"),
        new FrequencyInfo(5760100000L, 5760100000L, "5.7GHz CaLing Frequency"),
        new FrequencyInfo(5760100001L, 5760299999L, "5.7GHz General"),
        new FrequencyInfo(5760300000L, 5760400000L, "5.7GHz Propagation Beacons"),
        new FrequencyInfo(10368000000L, 10368099999L, "10GHz General"),
        new FrequencyInfo(10368100000L, 10368100000L, "10GHz CaLing Frequency"),
        new FrequencyInfo(10368100001L, 10368400000L, "10GHz General"),
        new FrequencyInfo(24192000000L, 24192099999L, "24GHz General"),
        new FrequencyInfo(24192100000L, 24192100000L, "24GHz CaLing Frequency"),
        new FrequencyInfo(24192100001L, 24192400000L, "24GHz General"),
        new FrequencyInfo(47088000000L, 47088099999L, "47GHz General"),
        new FrequencyInfo(47088100000L, 47088100000L, "47GHz CaLing Frequency"),
        new FrequencyInfo(47088100001L, 47088400000L, "47GHz General"),};


    private long start;
    private long stop;
    String label;

    public FrequencyInfo(long start, long stop, String label) {
        this.start = start;
        this.stop = stop;
        this.label = label;
        if (start == stop) System.err.println("EQ Freq: " + label);
    }

    public String getLabel() {
        return label;
    }

    public long getStart() {
        return start;
    }

    public long getStop() {
        return stop;
    }

    public static FrequencyInfo get(long freq) {
        for (int i = 0; i < frequencyInfo.length; i++) {
            FrequencyInfo info = frequencyInfo[i];
            if (info.start <= freq && freq <= info.stop) return info;
        }
        return null;
    }

    public static FrequencyInfo prev(FrequencyInfo f) {
        for (int i = 0; i < frequencyInfo.length; i++) {
            FrequencyInfo info = frequencyInfo[i];
            if (info == f) {
                if (i == 0) return f;
                return frequencyInfo[i - 1];
            }
        }
        return null;
    }

    public static FrequencyInfo next(FrequencyInfo f) {
        for (int i = 0; i < frequencyInfo.length; i++) {
            FrequencyInfo info = frequencyInfo[i];
            if (info == f) {
                if (i == frequencyInfo.length - 1) return f;
                return frequencyInfo[i + 1];
            }
        }
        return null;
    }

}
