package com.github.utkarsh.grpc.greeting.server;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc.GreetServiceImplBase;
import com.proto.greet.Greeting;
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
}