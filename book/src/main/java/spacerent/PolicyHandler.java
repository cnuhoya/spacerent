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
    @Autowired BookRepository bookRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRegistered_Updatestate(@Payload Registered registered){

        if(!registered.validate()) return;

        System.out.println("\n\n##### listener Updatestate : " + registered.toJson() + "\n\n");

        // Sample Logic //
        Book book = new Book();
        bookRepository.save(book);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRegistercancelled_Updatestate(@Payload Registercancelled registercancelled){

        if(!registercancelled.validate()) return;

        System.out.println("\n\n##### listener Updatestate : " + registercancelled.toJson() + "\n\n");

        // Sample Logic //
        Book book = new Book();
        bookRepository.save(book);
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
