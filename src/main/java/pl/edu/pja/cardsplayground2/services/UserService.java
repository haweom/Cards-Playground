package pl.edu.pja.cardsplayground2.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pja.cardsplayground2.dtos.UserDTO;
import pl.edu.pja.cardsplayground2.entities.Card;
import pl.edu.pja.cardsplayground2.entities.Deck;
import pl.edu.pja.cardsplayground2.entities.UserEntity;
import pl.edu.pja.cardsplayground2.repositories.UserRepository;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean logIn(UserDTO userDTO) {
        Optional<UserEntity> user = userRepository.findByUsername(userDTO.getUsername());

        if(user.isPresent()){
            if(passwordEncoder.matches(userDTO.getPassword(),user.get().getPassword())){
                userDTO.setId(user.get().getId());
                return true;
            }
        }
        return false;
    }

    public UserDTO copy(UserEntity user){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setId(user.getId());
        return userDTO;
    }

    public UserEntity extractUser(UserDTO dto){
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        return user;
    }

    public UserEntity getUser(Long id) {
        Optional<UserEntity> user = userRepository.findById(id);
        return user.orElse(null);
    }

    public UserDTO getUserDto(Long userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        return user.map(this::copy).orElse(null);
    }

    @Transactional
    public void bindCard(Card card, UserEntity user) {
        user.addCard(card);
        userRepository.save(user);
    }

    public void bindDeck(Deck deck, UserEntity user) {
        user.addDeck(deck);
        userRepository.save(user);
    }

    public boolean register(UserEntity user) {
        Optional<UserEntity> usernameCheck = userRepository.findByUsername(user.getUsername());

        if(usernameCheck.isEmpty()){
            try{
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
                return true;
            }catch (Exception e){
                return false;
            }
        }
        return false;
    }

    public boolean checkOwner(Card card, UserEntity user) {
        return user.getCards().contains(card);
    }

    public UserEntity findByUsername(String username) {
        Optional<UserEntity> user = userRepository.findByUsername(username);;
        return user.orElse(null);
    }

    public boolean checkOwnerDeck(Deck deck, UserEntity user) {
        return user.getDecks().contains(deck);
    }
}
