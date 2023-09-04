package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        User user=userRepository.findById(subscriptionEntryDto.getUserId()).get();

        int price=0;

        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC)){
            price=500;
        } else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO)){
            price=800+500;
        }else{
            price=1000+800+500;
        }

        Subscription subscription=new Subscription(subscriptionEntryDto.getSubscriptionType(),subscriptionEntryDto.getNoOfScreensRequired(),new Date(),price);

        user.setSubscription(subscription);

        subscription.setUser(user);

        subscriptionRepository.save(subscription);
        return price; //null
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user=userRepository.findById(userId).get();

        if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.ELITE)){
            throw new Exception ("Already the best Subscription");
        }

        Subscription subscription=user.getSubscription();
        int diff=0;

        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){

            subscription.setSubscriptionType(SubscriptionType.PRO);
            diff= 800-500;
        }else{
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            diff=1200-1000;
        }

        subscriptionRepository.save(subscription);

        return diff; //null
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription>subscriptionList=subscriptionRepository.findAll();
        int count=0;

        for(Subscription subscription:subscriptionList){
            count+=subscription.getTotalAmountPaid();
        }
        return count;
    }

}
