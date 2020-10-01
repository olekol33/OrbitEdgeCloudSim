package com.orbit.storageedgecloudsim;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RedisListHandler {
    //Takes list from ObjectGenerator and creates KV pairs in Redis
    public static void createList(){
        Jedis jedis = new Jedis("localhost", 6379);
//        List<List<Map>> listOfStripes = ObjectGenerator.getListOfStripes();
        ObjectGenerator_previous OG = new ObjectGenerator_previous(50,100,2,1);
        List<List<Map>> listOfStripes = OG.getListOfStripes();
        int i = 0;
        for (List<Map> stripe : listOfStripes){
            for (Map<String,String> KV : stripe){
                jedis.hmset("object:"+KV.get("id"),KV);
            }
        }
        jedis.close();
        //Closing
//        closeConnection();

    }
    //Terminate the connection
    public static void closeConnection(){
        Jedis jedis = new Jedis("localhost", 6379);
        jedis.flushAll();
        jedis.quit();
    }
    //Get key list by pattern
    public static List<String> getObjectsFromRedis(String pattern){
        Jedis jedis = new Jedis("localhost", 6379);
        int batch = 100;
        List<String> listOfObjects = new ArrayList<>();
        //Scan in batches
        ScanParams scanParams = new ScanParams().count(batch).match(pattern);
        String cur = redis.clients.jedis.ScanParams.SCAN_POINTER_START;
        do {
            ScanResult<String> scanResult = jedis.scan(cur, scanParams);
            listOfObjects = Stream.concat(listOfObjects.stream(), scanResult.getResult().stream())
                    .collect(Collectors.toList());
            cur = scanResult.getCursor();
        } while (!cur.equals(redis.clients.jedis.ScanParams.SCAN_POINTER_START));
        jedis.close();
        return listOfObjects;

    }

    public static String getObjectLocations(String objectID){
        Jedis jedis = new Jedis("localhost", 6379);
        String locations =  jedis.hget(objectID,"locations");
        jedis.close();
        return locations;
    }

    public static String getObjectSize(String objectID){
        Jedis jedis = new Jedis("localhost", 6379);
        String size =  jedis.hget(objectID,"size");
        return size;
    }
    //Returns data object IDs in index 0 and parity in index 1
    public static String[] getStripeObjects(String metadataID){
        Jedis jedis = new Jedis("localhost", 6379);
        String dataObjects = jedis.hget(metadataID,"data");
        String parityObjects = jedis.hget(metadataID,"parity");
        return new String[] {dataObjects,parityObjects};

    }
    //Returns random list of stripes
    public static List<String> getRandomStripeListForDevice(int numOfStripesToRead, int seed){
        List<String> listOfMetadataObjects = getObjectsFromRedis("object:md*");
        List<String> listForDevice = new ArrayList<>(numOfStripesToRead);
        Random random = new Random();
        random.setSeed(seed);
        for (int i=0; i<numOfStripesToRead; i++) {
            //Get index such that 0<=index<= size of list
            int metadataIndex = random.nextInt(listOfMetadataObjects.size()+1);
            listForDevice.add(listOfMetadataObjects.get(metadataIndex));
        }
        return listForDevice;
    }
}
