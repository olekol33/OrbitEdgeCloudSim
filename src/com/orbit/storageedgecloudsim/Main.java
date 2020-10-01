package com.orbit.storageedgecloudsim;

public class Main {

    public static void main(String[] args) {
        //TESTING
		RedisListHandler.createList();
//		RedisListHandler.getObjectsFromRedis("object:md*");
//		System.out.println(RedisListHandler.getObjectLocations("object:d1"));
		System.out.println(RedisListHandler.getRandomStripeListForDevice(15,42));
//		RedisListHandler.closeConnection();

    }
}
