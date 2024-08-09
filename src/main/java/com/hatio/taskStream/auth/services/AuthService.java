package com.hatio.taskStream.auth.services;



import com.hatio.taskStream.auth.entities.User;
import com.hatio.taskStream.auth.repositories.UserRepository;
import com.hatio.taskStream.auth.utils.AuthResponse;
import com.hatio.taskStream.auth.utils.LoginRequest;
import com.hatio.taskStream.auth.utils.RegisterRequest;
import com.hatio.taskStream.auth.utils.RegisterResponse;
import com.hatio.taskStream.enums.UserRole;
import com.hatio.taskStream.exception.BadCredentialException;
import com.hatio.taskStream.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private static final Logger logger = LoggerFactory.getLogger( AuthService.class);


    public RegisterResponse register(RegisterRequest registerRequest) {
        logger.info("Registering new user with email: {}", registerRequest.getEmail());

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            logger.error("Registration failed: User with email {} already exists.", registerRequest.getEmail());
            throw new UserAlreadyExistsException("User with the same email already exists.");
        }

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            logger.error("Registration failed: User with username {} already exists.", registerRequest.getUsername());
            throw new UserAlreadyExistsException("User with the same username already exists.");
        }

        try {
            // Create and save new user
            User user = User.builder()
                    .name(registerRequest.getName())
                    .email(registerRequest.getEmail())
                    .username(registerRequest.getUsername())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(UserRole.USER)
                    .build();


            User savedUser = userRepository.save(user);
            RegisterResponse response = new RegisterResponse(
                    savedUser.getUserId(),
                    savedUser.getName(),
                    savedUser.getEmail(),
                    savedUser.getUsername(),
                    "Registration successful"
            );

            logger.info("Successfully registered new user with id: {}", savedUser.getUserId());
            return response;

        } catch (Exception e) {
            logger.error("Registration failed due to unexpected error: {}", e.getMessage());
            throw new RuntimeException("An unexpected error occurred during registration.", e);
        }
    }


    public AuthResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            logger.error("Authentication failed for email: {}", loginRequest.getEmail());
            throw new BadCredentialException("Invalid email or password.");
        }

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + loginRequest.getEmail()));
        logger.info("User authenticated: {}", loginRequest.getEmail());

        String accessToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(loginRequest.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

}
