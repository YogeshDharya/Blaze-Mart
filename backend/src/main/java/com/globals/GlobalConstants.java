package com.globals;

import lombok.Getter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class GlobalConstants {

  public static final String REDIS_HOST = "localhost";
  public static final int REDIS_PORT = 6379;


  public static final int REDIS_ENTRY_EXPIRY_IN_SECONDS = 3600;

  public static final String EXCHANGE_NAME = "rabbitmq-exchange";
  public static final String QUEUE_NAME = "rabbitmq-queue";
  public static final String ROUTING_KEY = "blazemart.postorder";

  @Getter
  private static JedisPool jedisPool;

  public static void initCache() {
    jedisPool = new JedisPool(
        new JedisPoolConfig(), REDIS_HOST, REDIS_PORT, REDIS_ENTRY_EXPIRY_IN_SECONDS);
  }


  public static boolean isCacheAvailable() {
    if (jedisPool == null) {
      return false;
    }

    return !jedisPool.isClosed();
  }


  public static void destroyCache() {
    try {
      jedisPool.getResource().flushAll();
    } catch (JedisConnectionException e) {
      System.out.println("Error");
    }
    jedisPool.destroy();
  }
}
