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

        System.out.println("\n\n##### listener Receivestatus : " + approved.toJson() + "\n\n");        
        Space space = spaceRepository.findByBookid(approved.getBookid()); 

//        space.setUserid(approved.getUserid());
//        space.setSpacename(approved.getSpacename());
//        space.setBookid(approved.getBookid());

        if(approved.getStatus() == "cancel-pay"){
           space.setStatus("cancel-register");          
        }
        else{
          space.setStatus("success-register");
        } 
                  
        spaceRepository.save(space);    
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
