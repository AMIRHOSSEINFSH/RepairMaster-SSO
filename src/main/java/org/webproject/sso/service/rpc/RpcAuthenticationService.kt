package org.webproject.sso.service.rpc

import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.beans.factory.annotation.Autowired
import org.webproject.grpcserver.proto.AuthenticationServiceGrpc
import org.webproject.grpcserver.proto.Models
import org.webproject.grpcserver.proto.Models.AuthenticationResponse
import org.webproject.sso.authentication.TokenManagement.parseToken
import org.webproject.sso.repository.SessionRepository
import org.webproject.sso.repository.UserRepository

@GrpcService
class RpcAuthenticationService @Autowired constructor(
    private val sessionRepository: SessionRepository
) : AuthenticationServiceGrpc.AuthenticationServiceImplBase() {


    override fun getUserAuthentication(
        request: Models.AuthenticationRequest,
        responseObserver: StreamObserver<AuthenticationResponse>
    ) {
        val itRawToken = request.token
        val token = parseToken(itRawToken)
        if (token.isTokenExpired() || request.deviceModel != token.deviceModel) {
            responseObserver.onNext(AuthenticationResponse.getDefaultInstance())
            responseObserver.onCompleted()
            return
        }

        val itSessionDb = sessionRepository.findByUserIdAndDeviceOwner(token.sessionId,request.deviceModel)

        if(itSessionDb == null) {
            responseObserver.onNext(AuthenticationResponse.getDefaultInstance())
            responseObserver.onCompleted()
            return
        }


        if (itSessionDb.user.token_count <= 0) {
            responseObserver.onNext(AuthenticationResponse.getDefaultInstance())
            responseObserver.onCompleted()
            return
        }

        val user = itSessionDb.user
        responseObserver.onNext(
            AuthenticationResponse.newBuilder()
                .setUserId(user.id.toString())
                .setType(user.type.id)
                .setFirstname(user.firstName)
                .setLastname(user.lastName)
                .setEmail(user.email)
                .build()
        )
        responseObserver.onCompleted()


        super.getUserAuthentication(request, responseObserver)
    }

}