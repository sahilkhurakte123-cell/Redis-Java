package com.sahilkhurakte.redis;

import com.sahilkhurakte.redis.server.RedisServer;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) throws IOException {

        int port = 6379;

        for(int i=0;i<args.length;i++) {
            if(args[i].equals("--port") && i+1<args.length) {
                port=Integer.parseInt(args[i+1]);
            }
        }

        new RedisServer(port).start();
    }
}