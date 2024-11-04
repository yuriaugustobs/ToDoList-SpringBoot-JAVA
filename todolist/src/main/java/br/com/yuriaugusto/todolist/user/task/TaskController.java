package br.com.yuriaugusto.todolist.user.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var currentDate = LocalDateTime.now();
        
        // Pega o ID do usuário que foi setado no FilterTaskAuth
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);
        
        // Validação da data de início
        if (currentDate.isAfter(taskModel.getStartAt())) {
            return ResponseEntity.badRequest()
                    .body("A data de início deve ser maior que a data atual");
        }

        // Validação da data de término
        if (currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.badRequest()
                    .body("A data de término deve ser maior que a data atual");
        }

        // Validação se a data de término é maior que a data de início
        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.badRequest()
                    .body("A data de início deve ser menor que a data de término");
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping("/")
    public ResponseEntity<List<TaskModel>> list(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser((UUID) idUser);
        return ResponseEntity.ok(tasks);
    }
    
    // passando id da task que eu desejo alterar
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {
        // Busca a tarefa
        var task = this.taskRepository.findById(id).orElse(null);
        
        if(task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tarefa não encontrada");
        }
        
        // Pega o ID do usuário do token
        var idUser = request.getAttribute("idUser");
        
        // Valida se a tarefa pertence ao usuário
        if(!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Você não tem permissão para alterar esta tarefa");
        }
        
        // Se chegou aqui, pode atualizar a tarefa
        if(taskModel.getTitle() != null) task.setTitle(taskModel.getTitle());
        if(taskModel.getDescription() != null) task.setDescription(taskModel.getDescription());
        if(taskModel.getStartAt() != null) task.setStartAt(taskModel.getStartAt());
        if(taskModel.getEndAt() != null) task.setEndAt(taskModel.getEndAt());
        if(taskModel.getPriority() != null) task.setPriority(taskModel.getPriority());
        
        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok().body(taskUpdated);
    }
  }