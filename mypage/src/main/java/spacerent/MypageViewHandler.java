package spacerent;

import spacerent.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MypageViewHandler {


    @Autowired
    private MypageRepository mypageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenApproved_then_CREATE_1 (@Payload Approved approved) {
        try {

            if (!approved.validate()) return;

            System.out.println("\n\n##### listener paid : " + approved.toJson() + "\n\n");


            Mypage mypage = new Mypage();
            mypage.setUserid(approved.getUserid());
            mypage.setBookid(approved.getBookid());       
            mypage.setSpacename(approved.getSpacename());
            mypage.setStatus(approved.getStatus());
            mypageRepository.save(mypage);
        
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenRegistered_then_UPDATE_1(@Payload Registered registered) {
        try {
            if (!registered.validate()) return;
            
            System.out.println("\n\n##### listener eduRegistered : " + registered.toJson() + "\n\n");

            Mypage mypage = mypageRepository.findByBookid(registered.getBookid());
            mypage.setStatus("Registered");
            mypageRepository.save(mypage);
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenEduAppCancelled_then_UPDATE_2(@Payload Registercancelled registercancelled) {
        try {
            if (!registercancelled.validate()) return;
            Mypage mypage = mypageRepository.findByBookid(registercancelled.getBookid());
            mypage.setStatus(registercancelled.getStatus());
            mypageRepository.save(mypage);
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}