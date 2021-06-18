package com.github.utkarsh.grpc.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.io.IOException;

public class CalculatorServer {

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Calculator server");

        Server server = ServerBuilder.forPort(50051)
            .addService(new CalculatorServiceImpl())
            .addService(ProtoReflectionService.newInstance()) //reflection
            .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received Shutdown Request");
            server.shutdown();
            System.out.println("Successfully stopped server");
        }));

        server.awaitTermination();
    }
}
