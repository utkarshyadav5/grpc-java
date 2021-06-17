package com.github.utkarsh.grpc.greeting.client;

import com.proto.greet.GreetEveryoneRequest;
import com.proto.greet.GreetEveryoneResponse;
import com.proto.greet.GreetManyTimesRequest;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.GreetWithDeadlineRequest;
import com.proto.greet.GreetWithDeadlineResponse;
import com.proto.greet.Greeting;
import com.proto.greet.LongGreetRequest;
import com.proto.greet.LongGreetResponse;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    private final ManagedChannel managedChannel;

    GreetingClient() {
        managedChannel = ManagedChannelBuilder.forAddress("localhost",50051)
            .usePlaintext()
            .build();
    }

    public static void main(String[] args) {
        System.out.println("Hello I'm a gRPC client");

        GreetingClient greetingClient = new GreetingClient();
        greetingClient.run();
    }

    private void run() {
//        doUnaryCall();
//        doServerStreamingCall();
//        doClientStreamingCall();
//        doBiDirectionalStreamingCall();

        doGreetWithDeadline();

        //Sync Client
        //DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(managedChannel);
        //Async Client
        //DummyServiceGrpc.DummyServiceFutureStub asyncClient = DummyServiceGrpc.newFutureStub(managedChannel);

        System.out.println("Shutting down channel");
        managedChannel.shutdown();
    }

    // -------------------------- UNARY --------------------------
    private void doUnaryCall() {
        //create a greet service client
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(managedChannel);

        // create the same for request
        GreetRequest greetRequest = GreetRequest.newBuilder()
            .setGreeting(Greeting.newBuilder().setFirstName("Utkarsh").setLastName("Yadav").build())
            .build();
        //call the RPC and get back a GreetResponse
        GreetResponse greetResponse = greetClient.greet(greetRequest);
        System.out.println(greetResponse.getResult());
    }

    // -------------------------- SERVER STREAM --------------------------
    private void doServerStreamingCall() {
        //create a greet service client
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(managedChannel);
        // create the proto buffer request
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
            .setGreeting(Greeting.newBuilder().setFirstName("Utkarsh").setLastName("Yadav").build())
            .build();
        // call server stream and get stream of response
        greetClient.greetManyTimes(greetManyTimesRequest)
            .forEachRemaining(System.out::println);
    }

    // -------------------------- CLIENT STREAM --------------------------
    private void doClientStreamingCall() {
        //create a greet service client
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(managedChannel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<>() {
            @Override
            public void onNext(LongGreetResponse value) {
                //we get the response from the server
                //in this case it'll be called only once
                System.out.println("Received response from server");
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                //we get an error from the server
            }

            @Override
            public void onCompleted() {
                //server has sent the complete data
                //onCompleted() will be called after onNext()
                System.out.println("Server has sent the response completely");
                latch.countDown();
            }
        });

        System.out.println("Sending message 1");
        requestObserver.onNext(LongGreetRequest.newBuilder()
            .setGreeting(Greeting.newBuilder().setFirstName("Utkarsh 1").build())
            .build());

        System.out.println("Sending message 2");
        requestObserver.onNext(LongGreetRequest.newBuilder()
            .setGreeting(Greeting.newBuilder().setFirstName("Utkarsh 2").build())
            .build());

        System.out.println("Sending message 3");
        requestObserver.onNext(LongGreetRequest.newBuilder()
            .setGreeting(Greeting.newBuilder().setFirstName("Utkarsh 3").build())
            .build());

        requestObserver.onCompleted();

        //Why do we need latch here?
        // If don't wait here then after sending request it won't wait for the response
        // So basically this latch is waiting for the server onCompleted() to be called.
        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // -------------------------- BIDIRECTIONAL STREAM --------------------------
    private void doBiDirectionalStreamingCall() {
        //create a greet service client
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(managedChannel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestObserver = asyncClient.greetEveryone(new StreamObserver<>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
                System.out.println("Received response from server : "+ value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                //server send error
            }

            @Override
            public void onCompleted() {
                System.out.println("Server has sent the response completely");
                latch.countDown();
            }
        });

        Arrays.asList("Utkarsh 1", "Utkarsh 2", "Utkarsh 3").forEach(name -> {
            System.out.println("Sending message :"+ name);
            requestObserver.onNext(GreetEveryoneRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName(name).build())
                .build());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        requestObserver.onCompleted();

        //Why do we need latch here?
        // If don't wait here then after sending request it won't wait for the response
        // So basically this latch is waiting for the server onCompleted() to be called.
        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // -------------------------- DEADLINE --------------------------
    private void doGreetWithDeadline() {
        GreetServiceGrpc.GreetServiceBlockingStub blockingStub = GreetServiceGrpc.newBlockingStub(managedChannel);

        //first call with 1000ms deadline
        try {
            GreetWithDeadlineResponse response = blockingStub.withDeadline(Deadline.after(1000, TimeUnit.MILLISECONDS))
                .greetWithDeadline(GreetWithDeadlineRequest.newBuilder()
                    .setGreeting(Greeting.newBuilder().setFirstName("Utkarsh").build())
                    .build());
            System.out.println(response.getResult());
        } catch (StatusRuntimeException ex) {
            if(ex.getStatus() == Status.DEADLINE_EXCEEDED) {
                System.out.println("Deadline has exceeded, we don't want the response");
            } else {
                ex.printStackTrace();
            }
        }

        //second call with 100ms deadline
        try {
            GreetWithDeadlineResponse response = blockingStub.withDeadline(Deadline.after(100, TimeUnit.MILLISECONDS))
                .greetWithDeadline(GreetWithDeadlineRequest.newBuilder()
                    .setGreeting(Greeting.newBuilder().setFirstName("Utkarsh").build())
                    .build());
            System.out.println(response.getResult());
        } catch (StatusRuntimeException ex) {
            if(ex.getStatus() == Status.DEADLINE_EXCEEDED) {
                System.out.println("Deadline has exceeded, we don't want the response");
            } else {
                ex.printStackTrace();
            }
        }

    }
}
