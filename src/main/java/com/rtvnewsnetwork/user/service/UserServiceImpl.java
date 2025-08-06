package com.rtvnewsnetwork.user.service;

import com.rtvnewsnetwork.transaction.model.TransactionModel;
import com.rtvnewsnetwork.user.model.User;
import com.rtvnewsnetwork.user.model.UserDto;
import com.rtvnewsnetwork.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService , UserDetailsService {
    @Autowired
    private  UserRepository userRepository;

    @Value("${default.password}")
    private String defaultPassword;

    @Override
    public User getUserById(String id) {
        return userRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("User not found with id: "+id));
    }

    @Override
    public User findByUsernameElseCreate(String phoneNumber) {
        try {
            // Retrieve user details by phone number (treated as username)
            UserDetails userDetails = loadUserByUsername(phoneNumber);
            return (User)userDetails ;
        } catch (UsernameNotFoundException ex) {
            // Use a default encoded password
            String encodedPassword = defaultPassword;

            // Assign the authority "USER"
            List<String> authorities = List.of("USER");

            // Create and save new user
            User newUser = new User();
            newUser.setPhoneNumber(phoneNumber);
            newUser.setPassword(encodedPassword);
            newUser.setAuthorities(authorities);
            userRepository.save(newUser);
            return newUser;
        }

    }

    @Override
    public User addUser(User user) {
        return null;
    }

    @Override
    public User updateUser(String id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("User not found with id: "+id));
        user.setName(userDto.getName());
        if(userDto.getGender()!=null){
            user.setGender(userDto.getGender());
        }
        if(userDto.getAge()!=null){
            user.setAge(userDto.getAge());
        }
        if(userDto.getEmail()!=null){
            user.setEmail(userDto.getEmail());
        }
        return userRepository.save(user);
    }

    @Override
    public User findByPhoneNumber(String phoneNumber) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    @Override
    public boolean updateUserWallet(TransactionModel transactionModel) {
        return userRepository.updateWallet(
                transactionModel.getUserId(),
                transactionModel.getTransactionType(),
                transactionModel.getAmount()
        );
    }

}
