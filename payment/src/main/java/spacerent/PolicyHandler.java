package spacerent;

import spacerent.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired PaymentRepository paymentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBookcancelled_Cancelpay(@Payload Bookcancelled bookcancelled){

        if(!bookcancelled.validate()) return;

        System.out.println("\n\n##### listener Cancelpay : " + bookcancelled.toJson() + "\n\n");

        // 결제 취소 상태 저장 
        System.out.println("\n\n###################################: " + bookcancelled.getBookid() + "\n\n");
        Payment payment = paymentRepository.findByBookid(bookcancelled.getBookid());
        System.out.println("\n\n###################################: " + bookcancelled.getBookid() + "\n\n");
        payment.setStatus("cancel-pay");
        System.out.println("\n\n###################################: " + payment.getStatus() + "\n\n");
        paymentRepository.save(payment);        
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
