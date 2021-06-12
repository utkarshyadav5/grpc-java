package com.github.utkarsh.grpc.calculator.server;

import com.proto.calculator.CalculatorServiceGrpc.CalculatorServiceImplBase;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        //Extract request
        int sum = request.getFirstNumber() + request.getSecondNumber();
        //Create the response
        SumResponse sumResponse = SumResponse.newBuilder().setResult(sum).build();

        responseObserver.onNext(sumResponse);
        responseObserver.onCompleted();
    }
}
