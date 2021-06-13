package com.github.utkarsh.grpc.greeting.client;

import com.proto.greet.GreetManyTimesRequest;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Hello I'm a gRPC client");

        ManagedChannel managedChannel = ManagedChannelBuilder
            .forAddress("localhost",50051)
            .usePlaintext()
            .build();

        System.out.println("Creating stub");
        //Sync Client
        //DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(managedChannel);
        //Async Client
        //DummyServiceGrpc.DummyServiceFutureStub asyncClient = DummyServiceGrpc.newFutureStub(managedChannel);


        //create a greet service client
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(managedChannel);

        // -------------------------- UNARY --------------------------
        // create the same for request
        GreetRequest greetRequest = GreetRequest.newBuilder()
            .setGreeting(Greeting.newBuilder().setFirstName("Utkarsh").setLastName("Yadav").build())
            .build();
        //call the RPC and get back a GreetResponse
        GreetResponse greetResponse = greetClient.greet(greetRequest);
        System.out.println(greetResponse.getResult());

        // -------------------------- SERVER STREAM --------------------------
        // create the proto buffer request
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
            .setGreeting(Greeting.newBuilder().setFirstName("Utkarsh").setLastName("Yadav").build())
            .build();
        // call server stream and get stream of response
        greetClient.greetManyTimes(greetManyTimesRequest)
            .forEachRemaining(System.out::println);

        System.out.println("Shutting down channel");
        managedChannel.shutdown();
    }
}
