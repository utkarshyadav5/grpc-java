package com.github.utkarsh.grpc.calculator.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.ComputeAverageRequest;
import com.proto.calculator.ComputeAverageResponse;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {

    private ManagedChannel channel;

    CalculatorClient() {
        channel = ManagedChannelBuilder.forAddress("localhost",50051)
            .usePlaintext()
            .build();
    }

    public static void main(String[] args) {
        System.out.println("Calculator client :");

        CalculatorClient calculatorClient = new CalculatorClient();
        calculatorClient.run();
    }

    private void run() {
        doSumCall();
        doAverageCall();

        System.out.println("Shutting down Calculator client :");
        channel.shutdown();
    }

    //Unary Stream for sum calculation
    private void doSumCall() {
        //create stub and client
        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorClient = CalculatorServiceGrpc.newBlockingStub(channel);

        SumRequest sumRequest = SumRequest.newBuilder().setFirstNumber(1).setSecondNumber(2).build();
        SumResponse sumResponse = calculatorClient.sum(sumRequest);
        System.out.println("Sum : "+ sumResponse.getResult());
    }

    //Client Stream for average calculation
    private void doAverageCall() {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<ComputeAverageRequest> requestObserver = asyncClient.computeAverage(new StreamObserver<>() {
            @Override
            public void onNext(ComputeAverageResponse value) {
                //we get the response from the server
                //in this case it'll be called only once
                System.out.println("Received response from server :"+ value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                //error from the server
            }

            @Override
            public void onCompleted() {
                //onCompleted() call from the server
                System.out.println("Server has sent the response completely");
                latch.countDown();
            }
        });

        for (int i=0; i<10000; i++) {
            System.out.println("Sending number :"+ i);
            requestObserver.onNext(ComputeAverageRequest.newBuilder()
                .setNumber(i)
                .build());
        }

        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}