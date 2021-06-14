package com.github.utkarsh.grpc.greeting.server;

import com.proto.greet.GreetEveryoneRequest;
import com.proto.greet.GreetEveryoneResponse;
import com.proto.greet.GreetManyTimesRequest;
import com.proto.greet.GreetManyTimesResponse;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc.GreetServiceImplBase;
import com.proto.greet.Greeting;
import com.proto.greet.LongGreetRequest;
import com.proto.greet.LongGreetResponse;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        //Extract request
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();

        //Create the response
        String result = "Hello, "+ firstName;
        GreetResponse greetResponse = GreetResponse.newBuilder()
            .setResult(result)
            .build();

        //Send the response
        responseObserver.onNext(greetResponse);
        //Complete the RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        String firstName = request.getGreeting().getFirstName();

        try {
            for (int i=0; i<10; i++) {
                //Create the response
                GreetManyTimesResponse greetResponse = GreetManyTimesResponse.newBuilder()
                    .setResult("Hello, "+ firstName + " response no: " +i)
                    .build();

                //Send the response
                responseObserver.onNext(greetResponse);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
        return new StreamObserver<>() {
            String result = "";

            @Override
            public void onNext(LongGreetRequest value) {
                //client send messages
                result += "Hello, "+ value.getGreeting().getFirstName() + "! ";
            }

            @Override
            public void onError(Throwable t) {
                //client sends error
            }

            @Override
            public void onCompleted() {
                //client is done
                responseObserver.onNext(
                    LongGreetResponse.newBuilder()
                        .setResult(result)
                        .build()
                );
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(GreetEveryoneRequest value) {
                String result = "Hello, "+ value.getGreeting().getFirstName() + "! ";
                GreetEveryoneResponse response = GreetEveryoneResponse.newBuilder()
                    .setResult(result)
                    .build();
                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable t) {
                //client sends error
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}