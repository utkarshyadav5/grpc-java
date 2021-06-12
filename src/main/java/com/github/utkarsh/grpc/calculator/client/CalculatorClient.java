package com.github.utkarsh.grpc.calculator.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.CalculatorServiceGrpc.CalculatorServiceBlockingStub;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {

    public static void main(String[] args) {
        System.out.println("Calculator client");

        ManagedChannel channel = ManagedChannelBuilder
            .forAddress("localhost",50051)
            .usePlaintext()
            .build();

        //create stub and client
        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorClient = CalculatorServiceGrpc.newBlockingStub(channel);

        SumRequest sumRequest = SumRequest.newBuilder().setFirstNumber(1).setSecondNumber(2).build();
        SumResponse sumResponse = calculatorClient.sum(sumRequest);

        System.out.println("Sum : "+ sumResponse.getResult());
        channel.shutdown();
    }
}