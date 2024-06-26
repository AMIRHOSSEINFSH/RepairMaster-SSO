package org.webproject.sso.service.rpc

import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.beans.factory.annotation.Autowired
import org.webproject.grpcserver.proto.AuthenticationServiceGrpc
import org.webproject.grpcserver.proto.Models
import org.webproject.grpcserver.proto.Models.AuthenticationResponse
import org.webproject.sso.authentication.TokenManagement.parseToken
import org.webproject.sso.repository.UserRepository

@GrpcService
class RpcAuthenticationService @Autowired constructor(
    private val userRepository: UserRepository
) : AuthenticationServiceGrpc.AuthenticationServiceImplBase() {


    override fun getUserAuthentication(
        request: Models.AuthenticationRequest,
        responseObserver: StreamObserver<AuthenticationResponse>
    ) {
        val itRawToken = request.token
        val token = parseToken(itRawToken)
        if (token.isTokenExpired()) {
            responseObserver.onNext(AuthenticationResponse.getDefaultInstance())
            responseObserver.onCompleted()
            return
        }
        val itUserDb = userRepository.findById(token.sessionId)
        if (!itUserDb.isPresent || itUserDb.get().token_count <= 0) {
            responseObserver.onNext(AuthenticationResponse.getDefaultInstance())
            responseObserver.onCompleted()
            return
        }
        val user = itUserDb.get()
        responseObserver.onNext(
            AuthenticationResponse.newBuilder()
                .setUserId(user.id.toString())
                .setType(user.type.id.toString())
                .setFirstname(user.firstName)
                .setLastname(user.lastName)
                .build()
        )
        responseObserver.onCompleted()


        super.getUserAuthentication(request, responseObserver)
    }

}