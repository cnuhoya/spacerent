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

        System.out.println("\n\n$$$$$$ APRROVED BOOK ID :  " + approved.getBookid() + "\n\n");
        System.out.println("\n\n$$$$$$ APRROVED BOOK ID :  " + approved.getSpacename() + "\n\n");
        System.out.println("\n\n$$$$$$ APRROVED BOOK ID :  " + approved.getStatus() + "\n\n");
        System.out.println("\n\n$$$$$$ APRROVED BOOK ID :  " + approved.getBookid() + "\n\n");
        System.out.println("\n\n$$$$$$ APRROVED BOOK ID :  " + approved.getBookid() + "\n\n");             
        
        if(approved.getStatus().equals("cancel-booking")){
          System.out.println("\n\n55555555555555555555555 : " + approved.getStatus() + "\n\n");
          Space space = spaceRepository.findByBookid(approved.getBookid());
          space.setUserid(approved.getUserid());         
          space.setSpacename(approved.getSpacename());         
          space.setBookid(approved.getBookid());                 
          space.setStatus("cancel-register");   
          spaceRepository.save(space);        
        }else{
          System.out.println("\n\n666666666666666666666 : " + approved.getStatus() + "\n\n");
          Space space = new Space();
          space.setStatus("success-register");
          space.setUserid(approved.getUserid());         
          space.setSpacename(approved.getSpacename());         
          space.setBookid(approved.getBookid());
          spaceRepository.save(space);                    
        } 
                  
    
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
