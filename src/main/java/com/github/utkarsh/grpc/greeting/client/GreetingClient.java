package com.github.utkarsh.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Hello I'm a gRPC client");

        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost",50051).build();

        System.out.println("Creating stub");
        //Sync Client
        DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(managedChannel);
        //Async Client
        //DummyServiceGrpc.DummyServiceFutureStub asyncClient = DummyServiceGrpc.newFutureStub(managedChannel);

        System.out.println("Shutting down channel");
        managedChannel.shutdown();
    }
}
