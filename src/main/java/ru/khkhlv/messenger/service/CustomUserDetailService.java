//package ru.khkhlv.messenger.service;
//
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//public class CustomUserDetailService implements UserDetailsService {
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // Загрузите пользователя из базы данных
//        if ("user".equals(username)) {
//            return User.withUsername("user")
//                    .password("password")
//                    .roles("USER")
//                    .build();
//        } else {
//            throw new UsernameNotFoundException("Пользователь не найден: " + username);
//        }
//    }
//}
