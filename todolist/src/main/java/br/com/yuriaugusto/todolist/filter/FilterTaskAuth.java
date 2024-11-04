package br.com.yuriaugusto.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.yuriaugusto.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter{

    //validar se o usuario tem autorização
    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull FilterChain chain
    ) throws ServletException, IOException {
            
        var servletPath = request.getServletPath();
        if (servletPath.startsWith("/tasks/")) {

            // pegar a autenticação (usuario e senha)
            var authorization = request.getHeader("Authorization");
            if (authorization == null || !authorization.startsWith("Basic ")) {
                response.sendError(401, "Cabeçalho de autorização inválido");
                return;
            }

            var user_password = authorization.substring("Basic".length()).trim();
            // decodificando a senha base64
            byte[] authDecoded = Base64.getDecoder().decode(user_password);
            String authString = new String(authDecoded);

            String[] credentials = authString.split(":");
            if (credentials.length != 2) {
                response.sendError(401, "Credenciais inválidas");
                return;
            }

            String username = credentials[0];
            String password = credentials[1];

            System.out.println("Username: " + username);
            System.out.println("Password: " + password);

            // validar o usuario
            var user = this.userRepository.findByUsername(username);
            if (user == null) {
                response.sendError(401, "Usuário não encontrado");
                return;
            } else {
                // validar a senha
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (!passwordVerify.verified) {
                    response.sendError(401, "Senha incorreta");
                    return;
                }

                // Se a senha estiver correta, define o ID do usuário
                request.setAttribute("idUser", user.getId());
            }

            chain.doFilter(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
