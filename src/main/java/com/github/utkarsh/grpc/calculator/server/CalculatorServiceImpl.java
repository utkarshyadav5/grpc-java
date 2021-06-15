package com.github.utkarsh.grpc.calculator.server;

import com.proto.calculator.CalculatorServiceGrpc.CalculatorServiceImplBase;
import com.proto.calculator.ComputeAverageRequest;
import com.proto.calculator.ComputeAverageResponse;
import com.proto.calculator.ComputeMaxRequest;
import com.proto.calculator.ComputeMaxResponse;
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

    @Override
    public StreamObserver<ComputeAverageRequest> computeAverage(StreamObserver<ComputeAverageResponse> responseObserver) {
        return new StreamObserver<>() {
            int count = 0;
            int sum = 0;

            @Override
            public void onNext(ComputeAverageRequest value) {
                //stream of request incoming
                count++;
                sum += value.getNumber();
            }

            @Override
            public void onError(Throwable t) {
                //error from client
            }

            @Override
            public void onCompleted() {
                //return response when incoming request has completed
                responseObserver.onNext(ComputeAverageResponse.newBuilder()
                    .setResult(sum/count)
                    .build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<ComputeMaxRequest> computeMax(StreamObserver<ComputeMaxResponse> responseObserver) {
        return new StreamObserver<>() {
            int max = 0;
            @Override
            public void onNext(ComputeMaxRequest value) {
                max = Math.max(max, value.getNumber());
                responseObserver.onNext(ComputeMaxResponse.newBuilder()
                    .setResult(max).build());
            }

            @Override
            public void onError(Throwable t) {
                //error from client
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
