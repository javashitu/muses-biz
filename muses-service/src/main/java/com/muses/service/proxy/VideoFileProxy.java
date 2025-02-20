package com.muses.service.proxy;

import com.muses.service.grpc.MyHelloGrpc;
import com.muses.service.grpc.Person;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

/**
 * @ClassName VideoFileProxy
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/12/2 18:59
 */
@Slf4j
@Component
public class VideoFileProxy {

    @GrpcClient("muses-engine")
    private MyHelloGrpc.MyHelloBlockingStub helloBlockingStub;

    public void convertVideo(){
        Person person = Person.newBuilder().setName("shitu").setAge(1984).setLength(100).build();

        Person result = helloBlockingStub.sayHello(person);
        log.info("receive the grpc result {}", result);
    }
}
