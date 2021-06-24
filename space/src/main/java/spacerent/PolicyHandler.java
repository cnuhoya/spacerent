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
    @Autowired SpaceRepository spaceRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverApproved_Receivestatus(@Payload Approved approved){

      
        if(!approved.validate()) return;
       
      
          Space space = new Space();
          space.setStatus("success-register");
          space.setUserid(approved.getUserid());         
          space.setSpacename(approved.getSpacename());         
          space.setBookid(approved.getBookid());
          spaceRepository.save(space);                    
    
                  
    
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCancelled_cancelspace(@Payload Cancelled cancelled){

      
        if(!cancelled.validate()) return;
          
        
        if(cancelled.getStatus().equals("cancel-pay")){
          Space space = spaceRepository.findByBookid(cancelled.getBookid());
          space.setStatus("cancel-register");   
          spaceRepository.save(space);        
        }
                  
    
    }    


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
